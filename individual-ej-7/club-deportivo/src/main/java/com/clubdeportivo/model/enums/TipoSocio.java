package com.clubdeportivo.model.enums;

/**
 * ========================================================================================
 * ENUM: TipoSocio
 * ========================================================================================
 * Clasifica la membresía del socio en el club deportivo:
 * - TITULAR: Socio principal responsable de la membresía y del pago de la cuota familiar.
 * - FAMILIAR: Socio con carnet propio que forma parte del grupo familiar de otro socio titular.
 * ========================================================================================
 */
public enum TipoSocio {
    TITULAR("Socio Titular", "primary", "bx-user-check"),
    FAMILIAR("Socio Familiar", "info", "bx-group");

    private final String descripcion;
    private final String colorBadge;
    private final String icono;

    TipoSocio(String descripcion, String colorBadge, String icono) {
        this.descripcion = descripcion;
        this.colorBadge = colorBadge;
        this.icono = icono;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getColorBadge() {
        return colorBadge;
    }

    public String getIcono() {
        return icono;
    }
}
