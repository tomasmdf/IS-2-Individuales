package com.empresa.compras.enums;

/**
 * Ciclo de vida de una Orden de Compra realizada a un proveedor mayorista.
 *
 *  PENDIENTE -> la orden fue registrada (cabecera + detalle) pero todavia
 *               NO impacto en el stock. Es el estado inicial.
 *  RECIBIDA  -> el proveedor entrego la mercaderia; en este punto la capa
 *               de Service incrementa el stock de cada producto del detalle.
 *               Es una transicion de UNICA VIA (no se puede volver atras).
 *  ANULADA   -> la orden se cancela antes de ser recibida. Si ya fue
 *               RECIBIDA no puede anularse (se valida en el Service).
 */
public enum EstadoOrdenCompra {
    PENDIENTE,
    RECIBIDA,
    ANULADA
}
