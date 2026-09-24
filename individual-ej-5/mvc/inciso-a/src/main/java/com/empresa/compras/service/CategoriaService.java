package com.empresa.compras.service;

import com.empresa.compras.dto.CategoriaDTO;

import java.util.List;

public interface CategoriaService {
    List<CategoriaDTO> listarTodas();
    CategoriaDTO buscarPorId(Long id);
    CategoriaDTO guardar(CategoriaDTO dto);
    void eliminar(Long id);
}
