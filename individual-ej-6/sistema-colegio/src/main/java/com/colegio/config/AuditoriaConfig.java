package com.colegio.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Bean AuditorAware<String> que le indica a la AUDITORÍA DE ENTIDADES
 * (@CreatedBy / @LastModifiedBy en model.entity.Auditable) quién es el
 * usuario "actual". Se resuelve leyendo el nombre de usuario (correo) del
 * docente autenticado en el SecurityContext de Spring Security. Si no hay
 * nadie autenticado (por ejemplo, el DataSeeder al arrancar la app), se usa
 * el valor "sistema".
 */
@Configuration
public class AuditoriaConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of("sistema");
            }
            return Optional.of(authentication.getName());
        };
    }
}
