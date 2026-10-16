package com.empresa.compras.security;

import com.empresa.compras.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Adaptador entre la Entity Usuario (nuestro modelo) y la interfaz
 * UserDetails que exige Spring Security. Expone el rol con el prefijo
 * "ROLE_" (convencion obligatoria del framework) y usa el flag "activo"
 * de nuestra tabla para habilitar/deshabilitar la cuenta.
 */
@RequiredArgsConstructor
public class UsuarioDetailsImpl implements UserDetails {

    private final Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
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
        // Un usuario dado de baja (activo=false) no puede iniciar sesion.
        return usuario.isActivo();
    }
}
