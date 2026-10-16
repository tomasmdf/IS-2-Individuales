package com.clubdeportivo.dto;

import com.clubdeportivo.model.enums.TipoAcceso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * ========================================================================================
 * DTO DE SOLICITUD: RegistroAccesoRequestDTO
 * ========================================================================================
 * Objeto de comando para registrar la entrada o salida de una persona en el club.
 * ========================================================================================
 */
public class RegistroAccesoRequestDTO {

    @NotBlank(message = "El DNI de la persona es obligatorio")
    private String dniPersona;

    @NotNull(message = "Debe indicar si es ENTRADA o SALIDA")
    private TipoAcceso tipoAcceso;

    private String puntoAcceso = "Molinete Principal";

    private String observaciones;

    public RegistroAccesoRequestDTO() {
    }

    public RegistroAccesoRequestDTO(String dniPersona, TipoAcceso tipoAcceso) {
        this.dniPersona = dniPersona;
        this.tipoAcceso = tipoAcceso;
    }

    public String getDniPersona() {
        return dniPersona;
    }

    public void setDniPersona(String dniPersona) {
        this.dniPersona = (dniPersona != null ? dniPersona.trim() : null);
    }

    public TipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(TipoAcceso tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
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
