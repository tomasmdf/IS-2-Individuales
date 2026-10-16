package com.colegio.mapper;

import org.springframework.stereotype.Component;

import com.colegio.dto.DocenteResponse;
import com.colegio.model.entity.Docente;

/**
 * CAPA MAPPER: convierte manualmente entre @Entity y DTO.
 * Se implementa "a mano" (en vez de con una librería como MapStruct) para
 * que la transformación quede explícita y comentada; en un proyecto más
 * grande esta clase se generaría automáticamente.
 */
@Component
public class DocenteMapper {

    public DocenteResponse toResponse(Docente d) {
        if (d == null) return null;
        return new DocenteResponse(
                d.getId(), d.getNombre(), d.getApellido(), d.getSexo(),
                d.getFechaNacimiento(), d.getCorreo(), d.getRol(), d.isActivo()
        );
    }
}
