package com.ejercicio.sistemaregistro.config;

import com.ejercicio.sistemaregistro.controller.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * =============================================================================
 * "SesionInterceptor" — Interceptor de seguridad (protección de rutas)
 * =============================================================================
 * Implementa HandlerInterceptor, una interfaz de Spring MVC que permite
 * ejecutar código ANTES (preHandle), DESPUÉS (postHandle) o al finalizar
 * (afterCompletion) el procesamiento de una petición por un controlador.
 *
 * En este caso se usa preHandle() para actuar como un "guardián": antes de
 * dejar pasar la petición hacia /home (o cualquier otra ruta privada que se
 * registre en WebConfig), se revisa si existe el atributo de sesión
 * "USUARIO_SESION". Si no existe, se corta la cadena (return false) y se
 * redirige al usuario a /login.
 *
 * Esta es una alternativa simplificada a usar Spring Security completo,
 * pensada para que el ejercicio se enfoque en los conceptos de MVC + ORM +
 * Thymeleaf pedidos en la consigna.
 * =============================================================================
 */
@Component
public class SesionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); // false = no crear una sesión nueva si no existe

        boolean haySesion = session != null && session.getAttribute(AuthController.SESSION_USUARIO) != null;

        if (!haySesion) {
            response.sendRedirect("/login");
            return false; // corta la ejecución: el controlador de /home nunca se llega a ejecutar
        }
        return true; // deja continuar la petición normalmente
    }
}
