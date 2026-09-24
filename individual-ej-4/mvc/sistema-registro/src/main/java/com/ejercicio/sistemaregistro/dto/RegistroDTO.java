package com.ejercicio.sistemaregistro.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * =============================================================================
 * DTO "RegistroDTO" (Data Transfer Object)
 * =============================================================================
 * Los DTO son objetos "planos" que se usan para transportar datos entre
 * capas SIN exponer directamente la entidad JPA (Usuario) a la vista.
 *
 * ¿Por qué no usar la entidad Usuario directamente en el formulario?
 *   - El formulario de registro pide "confirmarClave" (para validar que el
 *     usuario tipeó bien su contraseña), campo que NO existe ni debe existir
 *     en la tabla de la base de datos.
 *   - Desacopla la Vista de la estructura interna de la base (buenas
 *     prácticas de arquitectura en capas: la Vista no debería conocer
 *     detalles de persistencia).
 *
 * Este DTO se enlaza (data binding) directamente con los campos del
 * formulario Thymeleaf mediante th:object / th:field en registro.html,
 * y Spring MVC lo valida automáticamente al llegar al controlador gracias
 * a la anotación @Valid sumada a las siguientes anotaciones de validación
 * (Jakarta Bean Validation):
 *
 *   @NotBlank : el campo no puede ser nulo ni contener solo espacios.
 *   @Email    : valida formato de correo electrónico.
 *   @Past     : la fecha debe ser anterior a la fecha actual (para nacimiento).
 *   @Size     : longitud mínima/máxima de un texto.
 * =============================================================================
 */
@Data
public class RegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre es demasiado largo")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido es demasiado largo")
    private String apellido;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "El documento es demasiado largo")
    private String documento;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El correo personal es obligatorio")
    @Email(message = "El correo personal no tiene un formato válido")
    private String correoPersonal;

    @NotBlank(message = "La clave es obligatoria")
    @Size(min = 6, max = 50, message = "La clave debe tener entre 6 y 50 caracteres")
    private String clave;

    @NotBlank(message = "Debe confirmar la clave")
    private String confirmarClave;


}
