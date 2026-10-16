package com.empresa.compras.service;

import com.empresa.compras.dto.OrdenCompraDTO;

import java.util.List;

public interface OrdenCompraService {
    List<OrdenCompraDTO> listarTodas();
    OrdenCompraDTO buscarPorId(Long id);

    /** Registra una nueva orden en estado PENDIENTE (todavia no impacta el stock). */
    OrdenCompraDTO registrar(OrdenCompraDTO dto, String usernameLogueado);

    /** Marca la orden como RECIBIDA e incrementa el stock de cada producto del detalle. */
    OrdenCompraDTO recibirOrden(Long id);

    /** Cancela una orden que aun no fue recibida. */
    OrdenCompraDTO anularOrden(Long id);
}
