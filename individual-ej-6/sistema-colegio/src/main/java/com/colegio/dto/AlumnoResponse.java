package com.colegio.dto;

import java.time.LocalDate;

import com.colegio.model.enums.Sexo;

public record AlumnoResponse(
        Long id,
        String nombre,
        String apellido,
        String dni,
        Sexo sexo,
        LocalDate fechaNacimiento,
        Long gradoId,
        String nombreGrado,
        Long aulaId,
        String nombreAula
) {
    public String nombreCompleto() {
        return apellido + ", " + nombre;
    }
}
