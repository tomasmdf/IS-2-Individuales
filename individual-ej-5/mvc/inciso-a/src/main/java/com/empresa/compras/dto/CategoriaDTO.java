package com.empresa.compras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) de Categoria.
 * Viaja entre Controller <-> Service <-> Vista Thymeleaf. Nunca se expone
 * la @Entity directamente a la capa web: esto desacopla el modelo de
 * persistencia de lo que efectivamente "ve" el formulario HTML.
 *
 * Las anotaciones de Bean Validation (@NotBlank, @Size, etc.) definen
 * restricciones DECLARATIVAS basicas de formato; la Vista las usa para
 * mostrar mensajes de error automaticos (th:errors), pero la VALIDACION DE
 * NEGOCIO definitiva (unicidad, reglas de la empresa) se hace siempre en
 * la capa de Service, tal como pide la consigna.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDTO {

    private Long id;

    @NotBlank(message = "El nombre de la categoria es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;

    @Size(max = 200, message = "La descripcion no puede superar los 200 caracteres")
    private String descripcion;
}
