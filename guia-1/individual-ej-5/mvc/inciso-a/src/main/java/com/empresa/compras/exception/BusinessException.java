package com.empresa.compras.exception;

/**
 * Excepcion de negocio (unchecked). La capa de Service la lanza cuando una
 * VALIDACION DE NEGOCIO falla (ej: CUIT de proveedor duplicado, orden de
 * compra sin detalle, stock insuficiente, etc.). Los Controllers la
 * capturan para mostrar el mensaje al usuario en la propia vista, sin
 * exponer stacktraces.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
