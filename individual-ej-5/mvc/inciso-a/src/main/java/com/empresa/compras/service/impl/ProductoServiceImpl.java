package com.empresa.compras.service.impl;

import com.empresa.compras.dto.ProductoDTO;
import com.empresa.compras.entity.Categoria;
import com.empresa.compras.entity.Producto;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.exception.ResourceNotFoundException;
import com.empresa.compras.mapper.ProductoMapper;
import com.empresa.compras.repository.CategoriaRepository;
import com.empresa.compras.repository.ProductoRepository;
import com.empresa.compras.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listarActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + id));
        return productoMapper.toDTO(producto);
    }

    @Override
    public ProductoDTO guardar(ProductoDTO dto) {
        boolean esAlta = dto.getId() == null;

        // --- Validaciones de negocio ---
        productoRepository.findByCodigo(dto.getCodigo().trim()).ifPresent(existente -> {
            if (esAlta || !existente.getId().equals(dto.getId())) {
                throw new BusinessException("Ya existe un producto con el codigo " + dto.getCodigo());
            }
        });

        if (dto.getPrecioVenta().compareTo(dto.getPrecioCompra()) < 0) {
            throw new BusinessException("El precio de venta no puede ser menor al precio de compra");
        }

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new BusinessException("La categoria seleccionada no existe"));

        Producto producto = productoMapper.toEntity(dto, categoria);
        if (esAlta) {
            producto.setActivo(true);
        }
        producto = productoRepository.save(producto);
        return productoMapper.toDTO(producto);
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + id));
        // Baja logica: preserva la integridad del historial de compras/ventas.
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    public void incrementarStock(Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new BusinessException("La cantidad a incrementar debe ser mayor a 0");
        }
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el producto con id " + productoId));
        producto.setStock(producto.getStock() + cantidad);
        productoRepository.save(producto);
    }
}
