package com.clubdeportivo.model.enums;

/**
 * ========================================================================================
 * ENUM: Parentesco
 * ========================================================================================
 * Define el vínculo o relación de parentesco de cada integrante del grupo familiar
 * respecto al socio titular.
 * ========================================================================================
 */
public enum Parentesco {
    CONYUGE("Cónyuge / Pareja"),
    HIJO("Hijo/a"),
    PADRE_MADRE("Padre / Madre"),
    HERMANO("Hermano/a"),
    OTRO("Otro Vínculo Familiar");

    private final String descripcion;

    Parentesco(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
