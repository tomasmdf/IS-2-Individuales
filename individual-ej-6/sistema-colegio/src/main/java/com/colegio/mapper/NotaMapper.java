package com.colegio.mapper;

import org.springframework.stereotype.Component;

import com.colegio.dto.NotaResponse;
import com.colegio.model.entity.Nota;

@Component
public class NotaMapper {

    public NotaResponse toResponse(Nota n) {
        if (n == null) return null;
        String alumno = n.getAlumno().getApellido() + ", " + n.getAlumno().getNombre();
        String docente = n.getAsignacion().getDocente().getApellido() + ", " + n.getAsignacion().getDocente().getNombre();
        return new NotaResponse(
                n.getId(), n.getAlumno().getId(), alumno,
                n.getAsignacion().getId(), n.getAsignacion().getMateria().getNombre(), docente,
                n.getPeriodo(), n.getValor(), n.getObservaciones(), n.getFechaCarga()
        );
    }
}
