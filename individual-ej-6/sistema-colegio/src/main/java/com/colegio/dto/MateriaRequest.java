package com.colegio.dto;

import jakarta.validation.constraints.NotBlank;

public record MateriaRequest(
        Long id,
        @NotBlank(message = "El nombre de la materia es obligatorio") String nombre,
        String descripcion
) {
}
