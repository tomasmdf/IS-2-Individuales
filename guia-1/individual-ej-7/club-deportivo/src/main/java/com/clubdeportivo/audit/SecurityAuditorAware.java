package com.clubdeportivo.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ========================================================================================
 * COMPONENTE DE AUDITORÍA: SecurityAuditorAware
 * ========================================================================================
 * Implementa la interfaz AuditorAware<T> de Spring Data JPA.
 *
 * Propósito:
 * Resuelve dinámicamente la identidad del usuario actual que ejecuta la transacción
 * consultando el contexto de seguridad (SecurityContextHolder).
 *
 * Flujo:
 * 1. Cuando Hibernate va a persistir o actualizar una entidad con @CreatedBy o @LastModifiedBy,
 *    invoca el método getCurrentAuditor() de este bean.
 * 2. Si hay un usuario autenticado (ej: "admin", "recepcionista"), se devuelve su nombre.
 * 3. Si la operación ocurre en el inicio de la app o por una tarea sin sesión, se asigna "SISTEMA".
 * ========================================================================================
 */
@Component("securityAuditorAware")
public class SecurityAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            // Usuario del sistema para procesos automáticos, semillas de datos o tareas batch
            return Optional.of("SISTEMA");
        }

        // Retorna el nombre de usuario autenticado en Spring Security
        return Optional.ofNullable(authentication.getName());
    }
}
