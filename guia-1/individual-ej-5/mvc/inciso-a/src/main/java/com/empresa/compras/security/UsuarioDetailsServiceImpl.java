package com.empresa.compras.security;

import com.empresa.compras.entity.Usuario;
import com.empresa.compras.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementacion de UserDetailsService: le indica a Spring Security COMO
 * buscar un usuario por su nombre de usuario (username) en la base de
 * datos, a traves del UsuarioRepository (ORM/JPA), cada vez que alguien
 * intenta iniciar sesion en el formulario de login.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña invalidos"));
        return new UsuarioDetailsImpl(usuario);
    }
}
