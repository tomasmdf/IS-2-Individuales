package com.empresa.compras.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @ControllerAdvice que se ejecuta antes de CADA request hacia un
 * @Controller. Se usa para inyectar en el Model datos transversales que
 * necesita el layout de la plantilla Sneat (navbar), como el nombre del
 * usuario actualmente logueado, sin repetir ese codigo en cada controller.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("usuarioLogueado")
    public String usuarioLogueado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return null;
    }

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
