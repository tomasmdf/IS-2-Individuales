package com.clubdeportivo.model.enums;

/**
 * ========================================================================================
 * ENUM: EstadoCuota
 * ========================================================================================
 * Indica el estado financiero del socio y su grupo familiar respecto a la cuota social.
 * - AL_DIA: La cuota del período vigente está pagada. Habilitado para ingresar al club.
 * - ADEUDA: No se ha registrado el pago del período vigente. Se emite alerta en el molinete.
 * - VENCIDA: Mantiene períodos anteriores impagos. Acceso condicionado o denegado.
 * ========================================================================================
 */
public enum EstadoCuota {
    AL_DIA("Al Día", "success", "bx-check-circle"),
    ADEUDA("Adeuda Mes Actual", "warning", "bx-error-circle"),
    VENCIDA("Cuota Vencida / Moroso", "danger", "bx-x-circle");

    private final String etiqueta;
    private final String colorBadge;
    private final String icono;

    EstadoCuota(String etiqueta, String colorBadge, String icono) {
        this.etiqueta = etiqueta;
        this.colorBadge = colorBadge;
        this.icono = icono;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getColorBadge() {
        return colorBadge;
    }

    public String getIcono() {
        return icono;
    }
}
