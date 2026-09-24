package com.empresa.compras.service;

import com.empresa.compras.dto.ProveedorDTO;

import java.util.List;

public interface ProveedorService {
    List<ProveedorDTO> listarTodos();
    List<ProveedorDTO> listarActivos();
    ProveedorDTO buscarPorId(Long id);
    ProveedorDTO guardar(ProveedorDTO dto);
    void eliminar(Long id);
}
