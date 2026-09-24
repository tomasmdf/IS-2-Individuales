package com.empresa.compras.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProveedorDTO {

    private Long id;

    @NotBlank(message = "La razon social es obligatoria")
    @Size(max = 150)
    private String razonSocial;

    @NotBlank(message = "El CUIT es obligatorio")
    @Size(max = 20)
    private String cuit;

    @Size(max = 30)
    private String telefono;

    @Email(message = "El email no tiene un formato valido")
    @Size(max = 100)
    private String email;

    @Size(max = 200)
    private String direccion;

    private boolean activo;
}
