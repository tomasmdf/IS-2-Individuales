package com.clubdeportivo.model.enums;

/**
 * ========================================================================================
 * ENUM: MedioPago
 * ========================================================================================
 * Define los canales de pago admitidos para la cancelación de la cuota social familiar.
 * Requerimiento explícito: "Efectivo, Transferencia, Mercado Pago".
 * ========================================================================================
 */
public enum MedioPago {
    EFECTIVO("Efectivo", "bx-money", "success"),
    TRANSFERENCIA("Transferencia Bancaria", "bx-transfer-alt", "info"),
    MERCADO_PAGO("Mercado Pago", "bx-credit-card", "primary");

    private final String etiqueta;
    private final String icono;
    private final String colorBadge;

    MedioPago(String etiqueta, String icono, String colorBadge) {
        this.etiqueta = etiqueta;
        this.icono = icono;
        this.colorBadge = colorBadge;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getIcono() {
        return icono;
    }

    public String getColorBadge() {
        return colorBadge;
    }
}
