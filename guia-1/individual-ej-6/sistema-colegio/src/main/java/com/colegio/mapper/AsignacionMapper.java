package com.colegio.mapper;

import org.springframework.stereotype.Component;

import com.colegio.dto.AsignacionResponse;
import com.colegio.model.entity.Asignacion;

@Component
public class AsignacionMapper {

    public AsignacionResponse toResponse(Asignacion a) {
        if (a == null) return null;
        String docente = a.getDocente().getApellido() + ", " + a.getDocente().getNombre();
        String etiquetaAula = a.getAula().getGrado().getNombre() + " \"" + a.getAula().getDivision() + "\"";
        return new AsignacionResponse(
                a.getId(),
                a.getDocente().getId(), docente,
                a.getMateria().getId(), a.getMateria().getNombre(),
                a.getAula().getId(), etiquetaAula
        );
    }
}
