package com.clubdeportivo.model.enums;

/**
 * ========================================================================================
 * ENUM: Rol
 * ========================================================================================
 * Perfiles de seguridad asignables a los operadores y usuarios del sistema.
 * Integrado directamente con los GrantedAuthority de Spring Security (prefijo ROLE_).
 * - ROLE_ADMIN: Acceso total al sistema, configuración, auditoría, usuarios, cobranzas y accesos.
 * - ROLE_RECEPCIONISTA: Acceso al registro de accesos perimetrales y módulo de cobranzas.
 * - ROLE_SOCIO: Consulta de estado de cuenta familiar y credencial digital.
 * ========================================================================================
 */
public enum Rol {
    ROLE_ADMIN("Administrador General"),
    ROLE_RECEPCIONISTA("Operador de Recepción y Cobranzas"),
    ROLE_SOCIO("Socio");

    private final String descripcion;

    Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
