package com.colegio.dto;

public record AulaResponse(
        Long id,
        String division,
        Integer capacidad,
        Long gradoId,
        String nombreGrado,
        int cantidadAlumnos
) {
    public String etiqueta() {
        return nombreGrado + " \"" + division + "\"";
    }
}
