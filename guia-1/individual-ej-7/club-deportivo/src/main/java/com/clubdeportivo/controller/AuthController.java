package com.clubdeportivo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ========================================================================================
 * CONTROLADOR MVC: AuthController
 * ========================================================================================
 * Gestiona el flujo de autenticación de usuarios (login y cierre de sesión).
 *
 * Anotaciones utilizadas:
 * - @Controller: Componente estereotipo de Spring MVC encargado de recibir peticiones HTTP,
 *   coordinar servicios de negocio y retornar nombres lógicos de vistas Thymeleaf.
 * - @GetMapping: Mapea solicitudes HTTP GET sobre rutas específicas.
 * ========================================================================================
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("mensajeError", "Credenciales incorrectas. Verifique su usuario y contraseña.");
        }
        if (logout != null) {
            model.addAttribute("mensajeExito", "Ha cerrado su sesión de forma segura.");
        }

        // Retorna la vista templates/auth/login.html con estilo Sneat
        return "auth/login";
    }
}
