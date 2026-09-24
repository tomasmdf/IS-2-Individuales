package com.empresa.compras.service;

import com.empresa.compras.dto.UsuarioDTO;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> listarTodos();
    UsuarioDTO buscarPorId(Long id);
    UsuarioDTO guardar(UsuarioDTO dto);
    void eliminar(Long id);
}
