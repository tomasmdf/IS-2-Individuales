package com.empresa.compras.service.impl;

import com.empresa.compras.dto.UsuarioDTO;
import com.empresa.compras.entity.Usuario;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.exception.ResourceNotFoundException;
import com.empresa.compras.mapper.UsuarioMapper;
import com.empresa.compras.repository.UsuarioRepository;
import com.empresa.compras.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UsuarioServiceImpl
 * ----------------------------------------------------------------------
 * Ademas de las validaciones de negocio habituales, esta clase es
 * responsable de ENCRIPTAR la contraseña con BCrypt (PasswordEncoder,
 * inyectado desde SecurityConfig) antes de persistirla. El hash resultante
 * es el que luego Spring Security compara contra lo tipeado en el login.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + id));
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioDTO guardar(UsuarioDTO dto) {
        boolean esAlta = dto.getId() == null;

        // --- Validaciones de negocio ---
        if (esAlta && (dto.getPassword() == null || dto.getPassword().isBlank())) {
            throw new BusinessException("La contraseña es obligatoria al crear un usuario");
        }

        usuarioRepository.findByUsername(dto.getUsername().trim()).ifPresent(existente -> {
            if (esAlta || !existente.getId().equals(dto.getId())) {
                throw new BusinessException("Ya existe un usuario con el nombre de usuario '" + dto.getUsername() + "'");
            }
        });

        Usuario usuario;
        if (esAlta) {
            usuario = Usuario.builder()
                    .username(dto.getUsername().trim())
                    .nombreCompleto(dto.getNombreCompleto())
                    .rol(dto.getRol())
                    .activo(true)
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .build();
        } else {
            usuario = usuarioRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + dto.getId()));
            usuario.setUsername(dto.getUsername().trim());
            usuario.setNombreCompleto(dto.getNombreCompleto());
            usuario.setRol(dto.getRol());
            usuario.setActivo(dto.isActivo());
            // Solo se re-encripta si el usuario cargo una contraseña nueva en el formulario.
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
        }

        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toDTO(usuario);
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + id));
        // Baja logica: un usuario dado de baja no puede autenticarse (ver UsuarioDetailsServiceImpl).
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
}
