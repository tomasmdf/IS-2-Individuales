package com.colegio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * =============================================================================
 * MANEJADOR GLOBAL DE EXCEPCIONES (@ControllerAdvice)
 * =============================================================================
 * Intercepta las excepciones lanzadas desde CUALQUIER @Controller de la
 * aplicación y las traduce a una vista de error amigable, evitando repetir
 * try/catch en cada método de cada controlador.
 * =============================================================================
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ModelAndView manejarNoEncontrado(RecursoNoEncontradoException ex) {
        ModelAndView mv = new ModelAndView("error/404");
        mv.addObject("mensaje", ex.getMessage());
        return mv;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({RegistroDuplicadoException.class, PasswordActualIncorrectaException.class})
    public ModelAndView manejarConflicto(RuntimeException ex) {
        ModelAndView mv = new ModelAndView("error/error-generico");
        mv.addObject("mensaje", ex.getMessage());
        return mv;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView manejarGenerico(Exception ex) {
        ModelAndView mv = new ModelAndView("error/error-generico");
        mv.addObject("mensaje", "Ocurrió un error inesperado. Intente nuevamente.");
        return mv;
    }
}
