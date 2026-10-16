package com.empresa.compras.dto;

import com.empresa.compras.enums.RolUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Usuario. El campo "password" solo se utiliza al DAR DE ALTA o al
 * CAMBIAR la contraseña; en los listados/ediciones nunca se envia el hash
 * de vuelta a la vista.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
    private String username;

    /** Solo obligatorio al crear; en edicion puede llegar vacio (no se modifica). */
    @Size(min = 6, max = 100, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 120)
    private String nombreCompleto;

    @NotNull(message = "Debe seleccionar un rol")
    private RolUsuario rol;

    private boolean activo;
}
