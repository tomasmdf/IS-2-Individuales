package com.colegio.exception;

/** Se lanza cuando se busca por ID una entidad (Docente, Alumno, etc.) que no existe. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
