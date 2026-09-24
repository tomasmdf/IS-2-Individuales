package com.empresa.compras.mapper;

import com.empresa.compras.dto.DetalleOrdenCompraDTO;
import com.empresa.compras.dto.OrdenCompraDTO;
import com.empresa.compras.entity.DetalleOrdenCompra;
import com.empresa.compras.entity.OrdenCompra;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrdenCompraMapper {

    public OrdenCompraDTO toDTO(OrdenCompra entity) {
        if (entity == null) return null;

        List<DetalleOrdenCompraDTO> detalles = entity.getDetalles().stream()
                .map(this::detalleToDTO)
                .collect(Collectors.toList());

        return OrdenCompraDTO.builder()
                .id(entity.getId())
                .numero(entity.getNumero())
                .fecha(entity.getFecha())
                .proveedorId(entity.getProveedor() != null ? entity.getProveedor().getId() : null)
                .proveedorNombre(entity.getProveedor() != null ? entity.getProveedor().getRazonSocial() : null)
                .usuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null)
                .usuarioNombre(entity.getUsuario() != null ? entity.getUsuario().getNombreCompleto() : null)
                .estado(entity.getEstado())
                .total(entity.getTotal())
                .observaciones(entity.getObservaciones())
                .detalles(detalles)
                .build();
    }

    public DetalleOrdenCompraDTO detalleToDTO(DetalleOrdenCompra d) {
        return DetalleOrdenCompraDTO.builder()
                .id(d.getId())
                .productoId(d.getProducto() != null ? d.getProducto().getId() : null)
                .productoNombre(d.getProducto() != null ? d.getProducto().getNombre() : null)
                .productoCodigo(d.getProducto() != null ? d.getProducto().getCodigo() : null)
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .subtotal(d.getSubtotal())
                .build();
    }
}
