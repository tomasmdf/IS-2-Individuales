package com.ejercicio.sistemaregistro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * =============================================================================
 * "WebConfig" — Configuración de Spring MVC
 * =============================================================================
 * @Configuration : marca la clase como fuente de definición de beans /
 *            configuración para el contenedor de Spring.
 *
 * Implementa WebMvcConfigurer para personalizar aspectos de Spring MVC sin
 * tener que sobreescribir toda la autoconfiguración de Spring Boot.
 *
 * Acá se registra el SesionInterceptor y se indica sobre qué rutas debe
 * actuar (addPathPatterns) y cuáles debe ignorar (excludePathPatterns):
 * las rutas públicas como /login, /registro, /css/**, /js/** NO deben
 * quedar bloqueadas por el interceptor de sesión.
 * =============================================================================
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SesionInterceptor sesionInterceptor;

    public WebConfig(SesionInterceptor sesionInterceptor) {
        this.sesionInterceptor = sesionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sesionInterceptor)
                .addPathPatterns("/home/**", "/perfil/**") // rutas privadas a proteger
                .excludePathPatterns(
                        "/login", "/registro", "/logout", "/",
                        "/css/**", "/js/**", "/img/**", "/webjars/**"
                );
    }
}
