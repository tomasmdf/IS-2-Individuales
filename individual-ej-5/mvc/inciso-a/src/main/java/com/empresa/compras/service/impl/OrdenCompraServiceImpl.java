package com.empresa.compras.service.impl;

import com.empresa.compras.dto.DetalleOrdenCompraDTO;
import com.empresa.compras.dto.OrdenCompraDTO;
import com.empresa.compras.entity.*;
import com.empresa.compras.enums.EstadoOrdenCompra;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.exception.ResourceNotFoundException;
import com.empresa.compras.mapper.OrdenCompraMapper;
import com.empresa.compras.repository.*;
import com.empresa.compras.service.OrdenCompraService;
import com.empresa.compras.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * OrdenCompraServiceImpl
 * ============================================================================
 * Contiene la logica de negocio MAS IMPORTANTE del sistema: el registro de
 * una compra a un proveedor mayorista y el impacto de dicha compra sobre
 * el STOCK de los productos (requisito central del enunciado).
 *
 * Reglas de negocio implementadas (validaciones en capa de Service):
 *  1) Toda orden debe tener proveedor valido y activo.
 *  2) Toda orden debe tener al menos un detalle (linea de compra).
 *  3) Cada producto del detalle debe existir y estar activo.
 *  4) La cantidad y el precio unitario de cada linea deben ser positivos.
 *  5) El subtotal de cada linea y el total de la orden se calculan en el
 *     Service (nunca se confia en un total que venga desde la vista).
 *  6) El stock de los productos SOLO se actualiza cuando la orden pasa a
 *     estado RECIBIDA (no al momento de registrarla), simulando el proceso
 *     real: primero se emite la orden de compra, y luego, cuando el
 *     proveedor efectivamente entrega la mercaderia, se recibe e impacta
 *     en el stock.
 *  7) Una orden RECIBIDA o ANULADA no puede volver a recibirse ni anularse.
 * ============================================================================
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenCompraRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;
    private final OrdenCompraMapper ordenCompraMapper;

    private static final DateTimeFormatter FORMATO_NUMERO = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional(readOnly = true)
    public List<OrdenCompraDTO> listarTodas() {
        return ordenCompraRepository.findAll().stream()
                .map(ordenCompraMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrdenCompraDTO buscarPorId(Long id) {
        OrdenCompra orden = obtenerOrdenOrFallar(id);
        return ordenCompraMapper.toDTO(orden);
    }

    @Override
    public OrdenCompraDTO registrar(OrdenCompraDTO dto, String usernameLogueado) {

        // --- Validacion: proveedor obligatorio y activo ---
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new BusinessException("El proveedor seleccionado no existe"));
        if (!proveedor.isActivo()) {
            throw new BusinessException("No se puede generar una orden de compra a un proveedor inactivo");
        }

        // --- Validacion: usuario logueado que emite la orden ---
        Usuario usuario = usuarioRepository.findByUsername(usernameLogueado)
                .orElseThrow(() -> new BusinessException("El usuario logueado no es valido"));

        // --- Validacion: debe existir al menos un renglon de detalle ---
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new BusinessException("La orden de compra debe tener al menos un producto en el detalle");
        }

        OrdenCompra orden = OrdenCompra.builder()
                .numero(generarNumeroOrden())
                .fecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now())
                .proveedor(proveedor)
                .usuario(usuario)
                .estado(EstadoOrdenCompra.PENDIENTE)
                .observaciones(dto.getObservaciones())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleOrdenCompraDTO detalleDTO : dto.getDetalles()) {

            // --- Validaciones de cada linea de detalle ---
            if (detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
                throw new BusinessException("La cantidad de cada producto debe ser mayor a 0");
            }
            if (detalleDTO.getPrecioUnitario() == null
                    || detalleDTO.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("El precio unitario de cada producto debe ser mayor a 0");
            }

            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new BusinessException("Uno de los productos seleccionados no existe"));
            if (!producto.isActivo()) {
                throw new BusinessException("El producto '" + producto.getNombre() + "' esta inactivo y no puede comprarse");
            }

            BigDecimal subtotal = detalleDTO.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));

            DetalleOrdenCompra detalle = DetalleOrdenCompra.builder()
                    .producto(producto)
                    .cantidad(detalleDTO.getCantidad())
                    .precioUnitario(detalleDTO.getPrecioUnitario())
                    .subtotal(subtotal)
                    .build();

            orden.agregarDetalle(detalle);
            total = total.add(subtotal);
        }

        orden.setTotal(total);
        orden = ordenCompraRepository.save(orden);
        return ordenCompraMapper.toDTO(orden);
    }

    @Override
    public OrdenCompraDTO recibirOrden(Long id) {
        OrdenCompra orden = obtenerOrdenOrFallar(id);

        // --- Validacion de transicion de estado ---
        if (orden.getEstado() != EstadoOrdenCompra.PENDIENTE) {
            throw new BusinessException("Solo una orden en estado PENDIENTE puede recibirse. Estado actual: " + orden.getEstado());
        }

        // --- Regla de negocio central: al recibir la orden se actualiza el STOCK ---
        for (DetalleOrdenCompra detalle : orden.getDetalles()) {
            productoService.incrementarStock(detalle.getProducto().getId(), detalle.getCantidad());
        }

        orden.setEstado(EstadoOrdenCompra.RECIBIDA);
        orden = ordenCompraRepository.save(orden);
        return ordenCompraMapper.toDTO(orden);
    }

    @Override
    public OrdenCompraDTO anularOrden(Long id) {
        OrdenCompra orden = obtenerOrdenOrFallar(id);

        if (orden.getEstado() == EstadoOrdenCompra.RECIBIDA) {
            throw new BusinessException("No se puede anular una orden que ya fue recibida (ya impacto en el stock)");
        }
        if (orden.getEstado() == EstadoOrdenCompra.ANULADA) {
            throw new BusinessException("La orden ya se encuentra anulada");
        }

        orden.setEstado(EstadoOrdenCompra.ANULADA);
        orden = ordenCompraRepository.save(orden);
        return ordenCompraMapper.toDTO(orden);
    }

    // ------------------------------------------------------------------
    // Metodos privados de apoyo
    // ------------------------------------------------------------------

    private OrdenCompra obtenerOrdenOrFallar(Long id) {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la orden de compra con id " + id));
    }

    /** Genera un numero correlativo simple con formato OC-AAAAMMDD-secuencia del dia. */
    private String generarNumeroOrden() {
        String prefijo = "OC-" + LocalDate.now().format(FORMATO_NUMERO) + "-";
        long cantidadHoy = ordenCompraRepository.count() + 1;
        String numero = prefijo + String.format("%04d", cantidadHoy);
        // Evita colisiones improbables si ya existe ese numero exacto.
        while (ordenCompraRepository.existsByNumero(numero)) {
            cantidadHoy++;
            numero = prefijo + String.format("%04d", cantidadHoy);
        }
        return numero;
    }
}
