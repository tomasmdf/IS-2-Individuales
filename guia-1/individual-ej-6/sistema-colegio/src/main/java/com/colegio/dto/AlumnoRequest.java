package com.colegio.dto;

import java.time.LocalDate;

import com.colegio.model.enums.Sexo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record AlumnoRequest(
        Long id,
        @NotBlank(message = "El nombre es obligatorio") String nombre,
        @NotBlank(message = "El apellido es obligatorio") String apellido,
        @NotBlank(message = "El DNI es obligatorio") String dni,
        @NotNull(message = "El sexo es obligatorio") Sexo sexo,
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        LocalDate fechaNacimiento,
        @NotNull(message = "Debe seleccionar un grado") Long gradoId,
        @NotNull(message = "Debe seleccionar un aula") Long aulaId
) {
}
