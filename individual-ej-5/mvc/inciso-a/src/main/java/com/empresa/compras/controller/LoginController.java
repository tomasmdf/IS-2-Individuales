package com.empresa.compras.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador (capa VISTA-CONTROLADOR) encargado unicamente de mostrar el
 * formulario de inicio de sesion. El procesamiento real del login
 * (usuario + contraseña) lo realiza Spring Security de forma automatica
 * contra "/login" (ver SecurityConfig), por lo que aqui NO hay logica de
 * autenticacion: solo se retorna la vista.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/login";
    }
}
