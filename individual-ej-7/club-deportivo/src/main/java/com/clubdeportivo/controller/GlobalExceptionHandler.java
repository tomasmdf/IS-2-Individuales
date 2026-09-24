package com.clubdeportivo.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * ========================================================================================
 * MANEJADOR GLOBAL DE EXCEPCIONES: GlobalExceptionHandler
 * ========================================================================================
 * Anotaciones utilizadas:
 * - @ControllerAdvice: Intercepta excepciones lanzadas por cualquier controlador MVC,
 *   centralizando el tratamiento de errores y evitando que la aplicación muestre la pantalla
 *   blanca genérica (Whitelabel Error Page).
 * ========================================================================================
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, Model model) {
        model.addAttribute("errorTitulo", "Archivo demasiado grande");
        model.addAttribute("errorDetalle", "La imagen de rostro excede el límite permitido de 5 MB.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception exc, Model model) {
        model.addAttribute("errorTitulo", "Error en el sistema");
        model.addAttribute("errorDetalle", exc.getMessage() != null ? exc.getMessage() : "Ocurrió un error inesperado.");
        return "error";
    }
}
