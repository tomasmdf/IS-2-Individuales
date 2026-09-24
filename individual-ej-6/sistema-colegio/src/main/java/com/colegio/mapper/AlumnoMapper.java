package com.colegio.mapper;

import org.springframework.stereotype.Component;

import com.colegio.dto.AlumnoResponse;
import com.colegio.model.entity.Alumno;

@Component
public class AlumnoMapper {

    public AlumnoResponse toResponse(Alumno a) {
        if (a == null) return null;
        return new AlumnoResponse(
                a.getId(), a.getNombre(), a.getApellido(), a.getDni(), a.getSexo(),
                a.getFechaNacimiento(),
                a.getGrado().getId(), a.getGrado().getNombre(),
                a.getAula().getId(), a.getAula().getDivision()
        );
    }
}
