package com.clubdeportivo.dto;

import com.clubdeportivo.model.enums.EstadoCuota;
import com.clubdeportivo.model.enums.TipoAcceso;
import java.time.LocalDateTime;

/**
 * ========================================================================================
 * DTO DE CONTROL DE ACCESO EN VIVO: PersonaAccesoDTO
 * ========================================================================================
 * Objeto proyectado para la terminal de control perimetral.
 * Cuando el operador/recepcionista digita o escanea el DNI de una persona en el molinete,
 * el sistema retorna este DTO conteniendo:
 * 1. La fotografía del rostro de la persona para cotejo visual del operador.
 * 2. Datos personales y si es socio titular o miembro del grupo familiar.
 * 3. Estado de la cuota familiar del mes en curso (AL_DIA, ADEUDA, VENCIDA).
 * 4. Indicador de presencia en tiempo real (si está dentro o fuera del club según su último registro).
 * ========================================================================================
 */
public class PersonaAccesoDTO {

    private String dni;
    private String nombreCompleto;
    private boolean esSocioTitular;
    private String rolDescripcion;
    private String socioTitularNombre;
    private Long socioId;
    private Long familiarId;
    private String fotoRostro;
    private EstadoCuota estadoCuota;
    private boolean habilitadoAcceso;
    private String mensajeHabilitacion;
    private boolean actualmenteAdentro;
    private TipoAcceso sugerenciaProximoAcceso;
    private LocalDateTime fechaUltimoAcceso;

    public PersonaAccesoDTO() {
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public boolean isEsSocioTitular() {
        return esSocioTitular;
    }

    public void setEsSocioTitular(boolean esSocioTitular) {
        this.esSocioTitular = esSocioTitular;
    }

    public String getRolDescripcion() {
        return rolDescripcion;
    }

    public void setRolDescripcion(String rolDescripcion) {
        this.rolDescripcion = rolDescripcion;
    }

    public String getSocioTitularNombre() {
        return socioTitularNombre;
    }

    public void setSocioTitularNombre(String socioTitularNombre) {
        this.socioTitularNombre = socioTitularNombre;
    }

    public Long getSocioId() {
        return socioId;
    }

    public void setSocioId(Long socioId) {
        this.socioId = socioId;
    }

    public Long getFamiliarId() {
        return familiarId;
    }

    public void setFamiliarId(Long familiarId) {
        this.familiarId = familiarId;
    }

    public String getFotoRostro() {
        return fotoRostro;
    }

    public void setFotoRostro(String fotoRostro) {
        this.fotoRostro = fotoRostro;
    }

    public EstadoCuota getEstadoCuota() {
        return estadoCuota;
    }

    public void setEstadoCuota(EstadoCuota estadoCuota) {
        this.estadoCuota = estadoCuota;
    }

    public boolean isHabilitadoAcceso() {
        return habilitadoAcceso;
    }

    public void setHabilitadoAcceso(boolean habilitadoAcceso) {
        this.habilitadoAcceso = habilitadoAcceso;
    }

    public String getMensajeHabilitacion() {
        return mensajeHabilitacion;
    }

    public void setMensajeHabilitacion(String mensajeHabilitacion) {
        this.mensajeHabilitacion = mensajeHabilitacion;
    }

    public boolean isActualmenteAdentro() {
        return actualmenteAdentro;
    }

    public void setActualmenteAdentro(boolean actualmenteAdentro) {
        this.actualmenteAdentro = actualmenteAdentro;
    }

    public TipoAcceso getSugerenciaProximoAcceso() {
        return sugerenciaProximoAcceso;
    }

    public void setSugerenciaProximoAcceso(TipoAcceso sugerenciaProximoAcceso) {
        this.sugerenciaProximoAcceso = sugerenciaProximoAcceso;
    }

    public LocalDateTime getFechaUltimoAcceso() {
        return fechaUltimoAcceso;
    }

    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }
}
