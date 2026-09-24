package com.colegio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para el cambio de contraseña de un docente ya autenticado
 * (requisito: "el sistema debe tener la posibilidad de cambiar la contraseña").
 */
public record CambiarPasswordRequest(

        @NotBlank(message = "Debe ingresar la contraseña actual")
        String passwordActual,

        @NotBlank(message = "Debe ingresar la nueva contraseña")
        @Size(min = 6, max = 100, message = "La nueva contraseña debe tener al menos 6 caracteres")
        String passwordNueva,

        @NotBlank(message = "Debe confirmar la nueva contraseña")
        String passwordConfirmacion
) {
    public boolean coinciden() {
        return passwordNueva != null && passwordNueva.equals(passwordConfirmacion);
    }
}
