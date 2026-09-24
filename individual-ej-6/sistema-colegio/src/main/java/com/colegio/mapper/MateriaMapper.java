package com.colegio.mapper;

import org.springframework.stereotype.Component;

import com.colegio.dto.MateriaResponse;
import com.colegio.model.entity.Materia;

@Component
public class MateriaMapper {

    public MateriaResponse toResponse(Materia m) {
        if (m == null) return null;
        return new MateriaResponse(m.getId(), m.getNombre(), m.getDescripcion());
    }
}
