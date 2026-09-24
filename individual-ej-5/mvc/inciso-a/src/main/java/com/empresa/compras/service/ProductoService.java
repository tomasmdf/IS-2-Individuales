package com.empresa.compras.service;

import com.empresa.compras.dto.ProductoDTO;

import java.util.List;

public interface ProductoService {
    List<ProductoDTO> listarTodos();
    List<ProductoDTO> listarActivos();
    ProductoDTO buscarPorId(Long id);
    ProductoDTO guardar(ProductoDTO dto);
    void eliminar(Long id);

    /** Utilizado internamente por OrdenCompraService al recibir una orden. */
    void incrementarStock(Long productoId, int cantidad);
}
