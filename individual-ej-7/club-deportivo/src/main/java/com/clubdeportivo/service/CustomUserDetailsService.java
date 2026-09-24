package com.clubdeportivo.service;

import com.clubdeportivo.model.Usuario;
import com.clubdeportivo.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * ========================================================================================
 * SERVICIO DE SEGURIDAD: CustomUserDetailsService
 * ========================================================================================
 * Implementa UserDetailsService de Spring Security.
 *
 * Flujo de Autenticación:
 * 1. Durante el intento de login, el DaoAuthenticationProvider de Spring Security invoca
 *    loadUserByUsername() pasando las credenciales enviadas por el formulario.
 * 2. Si el usuario existe y está activo, se construye un objeto UserDetails con su hash BCrypt
 *    y su lista de GrantedAuthority (roles).
 * 3. Spring Security compara el hash de la contraseña en memoria sin desencriptar.
 * ========================================================================================
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        if (!usuario.isActivo()) {
            throw new UsernameNotFoundException("La cuenta del usuario se encuentra inactiva");
        }

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(usuario.getRol().name()))
        );
    }
}
