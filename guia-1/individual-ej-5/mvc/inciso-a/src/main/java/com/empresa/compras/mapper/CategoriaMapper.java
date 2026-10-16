package com.empresa.compras.mapper;

import com.empresa.compras.dto.CategoriaDTO;
import com.empresa.compras.entity.Categoria;
import org.springframework.stereotype.Component;

/**
 * Mapper manual Entity <-> DTO.
 * Se implementa "a mano" (sin MapStruct) para que la conversion quede
 * explicita y facil de seguir en el marco del trabajo practico.
 */
@Component
public class CategoriaMapper {

    public CategoriaDTO toDTO(Categoria entity) {
        if (entity == null) return null;
        return CategoriaDTO.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .build();
    }

    public Categoria toEntity(CategoriaDTO dto) {
        return Categoria.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .build();
    }
}
