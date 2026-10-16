package com.clubdeportivo.model.enums;

/**
 * ========================================================================================
 * ENUM: TipoAcceso
 * ========================================================================================
 * Representa el tipo de movimiento registrado en el molinete o punto de acceso perimetral.
 * - ENTRADA: La persona (socio o familiar) ingresa a las instalaciones del club.
 * - SALIDA: La persona egresa de las instalaciones del club.
 * ========================================================================================
 */
public enum TipoAcceso {
    ENTRADA("Entrada"),
    SALIDA("Salida");

    private final String descripcion;

    TipoAcceso(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
