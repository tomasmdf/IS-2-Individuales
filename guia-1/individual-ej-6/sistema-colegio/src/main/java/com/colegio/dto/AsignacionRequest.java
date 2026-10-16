package com.colegio.dto;

import jakarta.validation.constraints.NotNull;

/** "El Docente X dicta la Materia Y en el Aula Z". */
public record AsignacionRequest(
        Long id,
        @NotNull(message = "Debe seleccionar un docente") Long docenteId,
        @NotNull(message = "Debe seleccionar una materia") Long materiaId,
        @NotNull(message = "Debe seleccionar un aula") Long aulaId
) {
}
