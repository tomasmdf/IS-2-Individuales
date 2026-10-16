package com.colegio.mapper;

import org.springframework.stereotype.Component;

import com.colegio.dto.GradoResponse;
import com.colegio.model.entity.Grado;

@Component
public class GradoMapper {

    public GradoResponse toResponse(Grado g) {
        if (g == null) return null;
        // g.getAulas() es LAZY: sólo es seguro llamarlo dentro de una transacción
        // (el Service que invoca este mapper está anotado @Transactional).
        return new GradoResponse(g.getId(), g.getNombre(), g.getNivel(), g.getAulas().size());
    }
}
