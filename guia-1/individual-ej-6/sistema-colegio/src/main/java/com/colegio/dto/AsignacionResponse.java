package com.colegio.dto;

public record AsignacionResponse(
        Long id,
        Long docenteId,
        String nombreDocente,
        Long materiaId,
        String nombreMateria,
        Long aulaId,
        String etiquetaAula
) {
}
