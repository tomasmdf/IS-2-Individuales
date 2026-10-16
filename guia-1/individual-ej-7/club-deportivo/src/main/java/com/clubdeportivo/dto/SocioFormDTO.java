package com.clubdeportivo.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * ========================================================================================
 * DTO DE ENTRADA / FORMULARIO: SocioFormDTO
 * ========================================================================================
 * Objeto de comando utilizado para recibir los datos del formulario HTML de alta o edición
 * de socios y aplicar validaciones declarativas mediante Jakarta Bean Validation.
 *
 * Anotaciones de validación utilizadas:
 * - @NotBlank: Asegura que el campo no sea nulo ni contenga únicamente espacios en blanco.
 * - @Size: Delimita la longitud mínima y máxima del texto.
 * - @Email: Aplica una expresión regular estándar para validar el formato de correo electrónico.
 * - @NotNull: Rechaza valores nulos.
 * - @Past: Exige que la fecha de nacimiento sea estrictamente anterior al día de la fecha.
 * ========================================================================================
 */
public class SocioFormDTO {

    private Long id;

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

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Debe proporcionar una dirección de email válida")
    @Size(max = 100, message = "El email no puede superar los 100 caracteres")
    private String email;

    @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
    private String telefono;

    @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
    private String direccion;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    /**
     * Archivo binario de la imagen del rostro subida desde el explorador de archivos
     * o capturada mediante la cámara web.
     */
    private MultipartFile fotoArchivo;

    /**
     * Referencia a la foto previamente existente (en caso de edición si no se sube una nueva).
     */
    private String fotoRostroActual;

    private boolean activo = true;

    private com.clubdeportivo.model.enums.TipoSocio tipoSocio = com.clubdeportivo.model.enums.TipoSocio.TITULAR;

    private Long socioTitularId;

    private com.clubdeportivo.model.enums.Parentesco parentesco;

    // ====================================================================================
    // GETTERS Y SETTERS
    // ====================================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email != null ? email.trim().toLowerCase() : null);
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public com.clubdeportivo.model.enums.TipoSocio getTipoSocio() {
        return tipoSocio;
    }

    public void setTipoSocio(com.clubdeportivo.model.enums.TipoSocio tipoSocio) {
        this.tipoSocio = (tipoSocio != null ? tipoSocio : com.clubdeportivo.model.enums.TipoSocio.TITULAR);
    }

    public Long getSocioTitularId() {
        return socioTitularId;
    }

    public void setSocioTitularId(Long socioTitularId) {
        this.socioTitularId = socioTitularId;
    }

    public com.clubdeportivo.model.enums.Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(com.clubdeportivo.model.enums.Parentesco parentesco) {
        this.parentesco = parentesco;
    }
}
