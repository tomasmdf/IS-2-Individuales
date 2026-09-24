package com.colegio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AulaRequest(
        Long id,
        @NotNull(message = "Debe seleccionar un grado") Long gradoId,
        @NotBlank(message = "La división es obligatoria") String division,
        @Positive(message = "La capacidad debe ser un número positivo") Integer capacidad
) {
}
