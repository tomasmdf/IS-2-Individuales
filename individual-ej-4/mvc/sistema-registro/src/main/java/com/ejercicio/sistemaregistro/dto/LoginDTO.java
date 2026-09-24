package com.ejercicio.sistemaregistro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * =============================================================================
 * DTO "LoginDTO"
 * =============================================================================
 * Objeto simple que representa los datos del formulario de inicio de sesión
 * (login.html): usuario (correo personal) y clave.
 * Se usa exclusivamente para el data-binding del formulario y su validación,
 * sin tocar directamente la entidad Usuario.
 * =============================================================================
 */
@Data
public class LoginDTO {

    @NotBlank(message = "Debe ingresar su usuario (correo personal)")
    private String correoPersonal;

    @NotBlank(message = "Debe ingresar su clave")
    private String clave;
}
