package com.colegio.dto;

import java.time.LocalDate;

import com.colegio.model.enums.Rol;
import com.colegio.model.enums.Sexo;

/** DTO de SALIDA: datos públicos de un docente (nunca incluye la contraseña). */
public record DocenteResponse(
        Long id,
        String nombre,
        String apellido,
        Sexo sexo,
        LocalDate fechaNacimiento,
        String correo,
        Rol rol,
        boolean activo
) {
    public String nombreCompleto() {
        return apellido + ", " + nombre;
    }
}
