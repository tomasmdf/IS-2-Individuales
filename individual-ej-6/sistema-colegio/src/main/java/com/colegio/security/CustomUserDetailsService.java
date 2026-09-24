package com.colegio.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.colegio.model.entity.Docente;
import com.colegio.repository.DocenteRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementación de UserDetailsService: es el "puente" que Spring Security
 * llama automáticamente en cada intento de login, pasándole el username
 * (correo) tipeado en el formulario. Aquí se busca ese correo en la base de
 * datos con el DocenteRepository (ORM) y se devuelve el adaptador
 * DocenteUserDetails para que Spring Security compare la contraseña.
 */
@Service
@RequiredArgsConstructor // Lombok: genera un constructor con los campos "final" -> inyección por constructor
public class CustomUserDetailsService implements UserDetailsService {

    private final DocenteRepository docenteRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Docente docente = docenteRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("No existe un docente con el correo: " + correo));
        return new DocenteUserDetails(docente);
    }
}
