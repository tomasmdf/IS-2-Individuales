package com.empresa.compras.mapper;

import com.empresa.compras.dto.ProductoDTO;
import com.empresa.compras.entity.Categoria;
import com.empresa.compras.entity.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoDTO toDTO(Producto entity) {
        if (entity == null) return null;
        return ProductoDTO.builder()
                .id(entity.getId())
                .codigo(entity.getCodigo())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .categoriaId(entity.getCategoria() != null ? entity.getCategoria().getId() : null)
                .categoriaNombre(entity.getCategoria() != null ? entity.getCategoria().getNombre() : null)
                .precioCompra(entity.getPrecioCompra())
                .precioVenta(entity.getPrecioVenta())
                .stock(entity.getStock())
                .stockMinimo(entity.getStockMinimo())
                .activo(entity.isActivo())
                .build();
    }

    /** La Categoria se resuelve en el Service (requiere consultar el repository). */
    public Producto toEntity(ProductoDTO dto, Categoria categoria) {
        return Producto.builder()
                .id(dto.getId())
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .categoria(categoria)
                .precioCompra(dto.getPrecioCompra())
                .precioVenta(dto.getPrecioVenta())
                .stock(dto.getStock())
                .stockMinimo(dto.getStockMinimo())
                .activo(dto.isActivo())
                .build();
    }
}
