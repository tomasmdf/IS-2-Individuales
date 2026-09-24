package com.colegio.exception;

/** Se lanza cuando, al cambiar la contraseña, la "contraseña actual" ingresada no coincide. */
public class PasswordActualIncorrectaException extends RuntimeException {
    public PasswordActualIncorrectaException(String mensaje) {
        super(mensaje);
    }
}
