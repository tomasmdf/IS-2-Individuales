package com.clubdeportivo.dto;

import com.clubdeportivo.model.enums.Parentesco;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * ========================================================================================
 * DTO DE FORMULARIO: FamiliarFormDTO
 * ========================================================================================
 * Encapsula la solicitud de alta o modificación de un integrante del grupo familiar.
 * ========================================================================================
 */
public class FamiliarFormDTO {

    private Long id;

    @NotNull(message = "El identificador del socio titular es obligatorio")
    private Long socioId;

    private String socioNombreCompleto;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 6, max = 15, message = "El DNI debe tener entre 6 y 15 caracteres")
    @Pattern(regexp = "^[0-9A-Za-z]+$", message = "El DNI solo debe contener caracteres alfanuméricos")
    private String dni;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 80, message = "El apellido debe tener entre 2 y 80 caracteres")
    private String apellido;

    @NotNull(message = "Debe seleccionar el tipo de parentesco")
    private Parentesco parentesco;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    /**
     * Fotografía facial para control de accesos perimetrales.
     */
    private MultipartFile fotoArchivo;

    private String fotoRostroActual;

    private boolean activo = true;

    // ====================================================================================
    // GETTERS Y SETTERS
    // ====================================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSocioId() {
        return socioId;
    }

    public void setSocioId(Long socioId) {
        this.socioId = socioId;
    }

    public String getSocioNombreCompleto() {
        return socioNombreCompleto;
    }

    public void setSocioNombreCompleto(String socioNombreCompleto) {
        this.socioNombreCompleto = socioNombreCompleto;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = (dni != null ? dni.trim() : null);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null ? nombre.trim() : null);
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = (apellido != null ? apellido.trim() : null);
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public MultipartFile getFotoArchivo() {
        return fotoArchivo;
    }

    public void setFotoArchivo(MultipartFile fotoArchivo) {
        this.fotoArchivo = fotoArchivo;
    }

    public String getFotoRostroActual() {
        return fotoRostroActual;
    }

    public void setFotoRostroActual(String fotoRostroActual) {
        this.fotoRostroActual = fotoRostroActual;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
