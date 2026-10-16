package com.colegio.exception;

/** Se lanza ante violaciones de unicidad de negocio (correo, DNI, división de aula, etc.). */
public class RegistroDuplicadoException extends RuntimeException {
    public RegistroDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
