package com.empresa.compras.exception;

/**
 * Excepcion lanzada cuando se busca una entidad por id y esta no existe
 * (ej: editar un producto que fue eliminado por otro usuario).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
