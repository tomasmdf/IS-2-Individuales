package com.colegio.dto;

import jakarta.validation.constraints.NotBlank;

public record GradoRequest(
        Long id,
        @NotBlank(message = "El nombre del grado es obligatorio") String nombre,
        @NotBlank(message = "El nivel es obligatorio") String nivel
) {
}
