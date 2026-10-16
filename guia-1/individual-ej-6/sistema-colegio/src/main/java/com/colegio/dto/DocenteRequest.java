package com.colegio.dto;

import java.time.LocalDate;

import com.colegio.model.enums.Sexo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

/**
 * DTO de ENTRADA para registrar/editar un Docente.
 * Al registrarse, el sistema:
 *   1) genera una contraseña inicial (o la que ingrese el administrador),
 *   2) la guarda con hash BCrypt,
 *   3) dispara el envío del correo de bienvenida al "correo" indicado.
 */
public record DocenteRequest(

        Long id, // null = alta nueva; con valor = edición

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        String apellido,

        @NotNull(message = "El sexo es obligatorio")
        Sexo sexo,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        LocalDate fechaNacimiento,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Ingrese un correo válido")
        String correo
) {
}
