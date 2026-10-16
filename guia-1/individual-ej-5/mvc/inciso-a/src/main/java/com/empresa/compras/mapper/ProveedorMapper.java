package com.empresa.compras.mapper;

import com.empresa.compras.dto.ProveedorDTO;
import com.empresa.compras.entity.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    public ProveedorDTO toDTO(Proveedor entity) {
        if (entity == null) return null;
        return ProveedorDTO.builder()
                .id(entity.getId())
                .razonSocial(entity.getRazonSocial())
                .cuit(entity.getCuit())
                .telefono(entity.getTelefono())
                .email(entity.getEmail())
                .direccion(entity.getDireccion())
                .activo(entity.isActivo())
                .build();
    }

    public Proveedor toEntity(ProveedorDTO dto) {
        return Proveedor.builder()
                .id(dto.getId())
                .razonSocial(dto.getRazonSocial())
                .cuit(dto.getCuit())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .direccion(dto.getDireccion())
                .activo(dto.isActivo())
                .build();
    }
}
