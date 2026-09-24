package com.colegio.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.colegio.model.entity.Docente;

/**
 * =============================================================================
 * ADAPTADOR de Docente (@Entity) hacia el contrato UserDetails de Spring
 * Security.
 * =============================================================================
 * Spring Security no sabe nada de la entidad "Docente"; sólo entiende la
 * interfaz UserDetails (username, password, authorities, y 4 flags de
 * estado de la cuenta). Esta clase "envuelve" un Docente y lo traduce a ese
 * contrato, manteniendo separada la capa de seguridad del modelo de dominio.
 *
 * getPassword() devuelve el HASH (BCrypt) guardado en la base, nunca la
 * contraseña real: Spring Security compara el hash contra lo que el
 * PasswordEncoder produce a partir de la contraseña tipeada en el login.
 * =============================================================================
 */
public class DocenteUserDetails implements UserDetails {

    private final Docente docente;

    public DocenteUserDetails(Docente docente) {
        this.docente = docente;
    }

    /** Acceso al Docente original, útil en los Controllers vía @AuthenticationPrincipal. */
    public Docente getDocente() {
        return docente;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Un único rol por docente -> una única GrantedAuthority ("ROLE_ADMIN" o "ROLE_DOCENTE").
        return List.of(new SimpleGrantedAuthority(docente.getRol().getAuthority()));
    }

    @Override
    public String getPassword() {
        return docente.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return docente.getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // La baja lógica (Docente.activo = false) impide el login sin borrar el registro.
        return docente.isActivo();
    }
}
