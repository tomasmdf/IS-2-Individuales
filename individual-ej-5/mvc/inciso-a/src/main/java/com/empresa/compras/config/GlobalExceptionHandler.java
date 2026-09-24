package com.empresa.compras.config;

import com.empresa.compras.exception.ResourceNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Manejo centralizado de excepciones no atrapadas por los controllers
 * (por ejemplo, un ResourceNotFoundException al acceder a una URL con un
 * id inexistente). Redirige a una vista de error generica y amigable en
 * lugar de mostrar el stacktrace por defecto de Spring/Whitelabel.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String manejarNoEncontrado(ResourceNotFoundException ex, Model model) {
        model.addAttribute("mensaje", ex.getMessage());
        return "error/no-encontrado";
    }
}
