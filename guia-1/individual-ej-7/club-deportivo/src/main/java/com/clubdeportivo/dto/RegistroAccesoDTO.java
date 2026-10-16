package com.clubdeportivo.dto;

import com.clubdeportivo.model.enums.EstadoCuota;
import com.clubdeportivo.model.enums.TipoAcceso;
import java.time.LocalDateTime;

/**
 * ========================================================================================
 * DTO DE RESPUESTA: RegistroAccesoDTO
 * ========================================================================================
 * Transporta los registros de auditoría de entradas y salidas hacia la vista del monitor.
 * ========================================================================================
 */
public class RegistroAccesoDTO {

    private Long id;
    private LocalDateTime fechaHora;
    private TipoAcceso tipoAcceso;
    private String tipoAccesoDescripcion;
    private String dniPersona;
    private String nombreCompletoPersona;
    private String fotoRostro;
    private boolean esSocioTitular;
    private Long socioId;
    private Long familiarId;
    private EstadoCuota estadoCuotaMomento;
    private String puntoAcceso;
    private String observaciones;

    public RegistroAccesoDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public TipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(TipoAcceso tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
        this.tipoAccesoDescripcion = (tipoAcceso != null ? tipoAcceso.getDescripcion() : "");
    }

    public String getTipoAccesoDescripcion() {
        return tipoAccesoDescripcion;
    }

    public void setTipoAccesoDescripcion(String tipoAccesoDescripcion) {
        this.tipoAccesoDescripcion = tipoAccesoDescripcion;
    }

    public String getDniPersona() {
        return dniPersona;
    }

    public void setDniPersona(String dniPersona) {
        this.dniPersona = dniPersona;
    }

    public String getNombreCompletoPersona() {
        return nombreCompletoPersona;
    }

    public void setNombreCompletoPersona(String nombreCompletoPersona) {
        this.nombreCompletoPersona = nombreCompletoPersona;
    }

    public String getFotoRostro() {
        return fotoRostro;
    }

    public void setFotoRostro(String fotoRostro) {
        this.fotoRostro = fotoRostro;
    }

    public boolean isEsSocioTitular() {
        return esSocioTitular;
    }

    public void setEsSocioTitular(boolean esSocioTitular) {
        this.esSocioTitular = esSocioTitular;
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

    public EstadoCuota getEstadoCuotaMomento() {
        return estadoCuotaMomento;
    }

    public void setEstadoCuotaMomento(EstadoCuota estadoCuotaMomento) {
        this.estadoCuotaMomento = estadoCuotaMomento;
    }

    public String getPuntoAcceso() {
        return puntoAcceso;
    }

    public void setPuntoAcceso(String puntoAcceso) {
        this.puntoAcceso = puntoAcceso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
