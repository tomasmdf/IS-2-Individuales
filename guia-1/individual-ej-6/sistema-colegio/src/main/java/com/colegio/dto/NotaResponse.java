package com.colegio.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.colegio.model.enums.Periodo;

public record NotaResponse(
        Long id,
        Long alumnoId,
        String nombreAlumno,
        Long asignacionId,
        String nombreMateria,
        String nombreDocente,
        Periodo periodo,
        BigDecimal valor,
        String observaciones,
        LocalDate fechaCarga
) {
}
