package com.empresa.compras.mapper;

import com.empresa.compras.dto.UsuarioDTO;
import com.empresa.compras.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    /** Nunca se copia el hash de la contraseña hacia el DTO de salida. */
    public UsuarioDTO toDTO(Usuario entity) {
        if (entity == null) return null;
        return UsuarioDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .nombreCompleto(entity.getNombreCompleto())
                .rol(entity.getRol())
                .activo(entity.isActivo())
                .build();
    }
}
