package com.clubdeportivo.dto;

import com.clubdeportivo.model.enums.EstadoCuota;
import java.time.LocalDate;
import java.time.Period;

/**
 * ========================================================================================
 * DTO DE RESPUESTA: SocioDTO
 * ========================================================================================
 * Objeto de Transferencia de Datos (DTO) que transporta la información de un socio titular
 * desde la capa de servicio hacia la capa de presentación (Controlador MVC y plantillas Thymeleaf).
 *
 * Principio arquitectónico:
 * Las entidades JPA nunca se exponen directamente a las vistas para:
 * 1. Prevenir la serialización accidental de datos sensibles o estructuras circulares.
 * 2. Evitar excepciones LazyInitializationException fuera del ámbito transaccional.
 * 3. Adaptar y formatear la información para el consumo directo de la interfaz gráfica.
 * ========================================================================================
 */
public class SocioDTO {

    private Long id;
    private String numeroSocio;
    private String dni;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String direccion;
    private LocalDate fechaNacimiento;
    private int edad;
    private LocalDate fechaAlta;
    private String fotoRostro;
    private boolean activo;
    private int cantidadFamiliares;
    private EstadoCuota estadoCuota = EstadoCuota.ADEUDA;
    private com.clubdeportivo.model.enums.TipoSocio tipoSocio = com.clubdeportivo.model.enums.TipoSocio.TITULAR;
    private Long socioTitularId;
    private String socioTitularNombreCompleto;
    private com.clubdeportivo.model.enums.Parentesco parentesco;
    private String parentescoDescripcion;

    public SocioDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroSocio() {
        return numeroSocio;
    }

    public void setNumeroSocio(String numeroSocio) {
        this.numeroSocio = numeroSocio;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        if (fechaNacimiento != null) {
            this.edad = Period.between(fechaNacimiento, LocalDate.now()).getYears();
        }
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public String getFotoRostro() {
        return fotoRostro;
    }

    public void setFotoRostro(String fotoRostro) {
        this.fotoRostro = fotoRostro;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getCantidadFamiliares() {
        return cantidadFamiliares;
    }

    public void setCantidadFamiliares(int cantidadFamiliares) {
        this.cantidadFamiliares = cantidadFamiliares;
    }

    public EstadoCuota getEstadoCuota() {
        return estadoCuota;
    }

    public void setEstadoCuota(EstadoCuota estadoCuota) {
        this.estadoCuota = estadoCuota;
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

    public String getSocioTitularNombreCompleto() {
        return socioTitularNombreCompleto;
    }

    public void setSocioTitularNombreCompleto(String socioTitularNombreCompleto) {
        this.socioTitularNombreCompleto = socioTitularNombreCompleto;
    }

    public com.clubdeportivo.model.enums.Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(com.clubdeportivo.model.enums.Parentesco parentesco) {
        this.parentesco = parentesco;
        this.parentescoDescripcion = (parentesco != null ? parentesco.getDescripcion() : null);
    }

    public String getParentescoDescripcion() {
        return parentescoDescripcion;
    }

    public void setParentescoDescripcion(String parentescoDescripcion) {
        this.parentescoDescripcion = parentescoDescripcion;
    }
}
