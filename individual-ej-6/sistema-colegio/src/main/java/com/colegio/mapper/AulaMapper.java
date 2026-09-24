package com.colegio.mapper;

import org.springframework.stereotype.Component;

import com.colegio.dto.AulaResponse;
import com.colegio.model.entity.Aula;

@Component
public class AulaMapper {

    public AulaResponse toResponse(Aula a) {
        if (a == null) return null;
        return new AulaResponse(
                a.getId(), a.getDivision(), a.getCapacidad(),
                a.getGrado().getId(), a.getGrado().getNombre(),
                a.getAlumnos().size()
        );
    }
}
