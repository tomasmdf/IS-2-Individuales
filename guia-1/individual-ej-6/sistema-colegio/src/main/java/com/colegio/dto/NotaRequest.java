package com.colegio.dto;

import java.math.BigDecimal;

import com.colegio.model.enums.Periodo;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotaRequest(
        Long id,
        @NotNull(message = "Debe indicar el alumno") Long alumnoId,
        @NotNull(message = "Debe indicar la asignación (materia/aula)") Long asignacionId,
        @NotNull(message = "Debe indicar el período") Periodo periodo,
        @NotNull(message = "Debe indicar la calificación")
        @DecimalMin(value = "1.00", message = "La nota mínima es 1.00")
        @DecimalMax(value = "10.00", message = "La nota máxima es 10.00")
        BigDecimal valor,
        @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
        String observaciones
) {
}
