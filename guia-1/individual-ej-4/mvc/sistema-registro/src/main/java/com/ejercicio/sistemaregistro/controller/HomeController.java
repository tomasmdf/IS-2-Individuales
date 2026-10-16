package com.ejercicio.sistemaregistro.controller;

import com.ejercicio.sistemaregistro.model.Usuario;
import com.ejercicio.sistemaregistro.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * =============================================================================
 * "HomeController" — CAPA CONTROLADOR
 * =============================================================================
 * Controla el panel principal (dashboard) al que se accede una vez logueado.
 * Esta ruta está protegida por el SesionInterceptor (config/SesionInterceptor):
 * si no hay un usuario en sesión, el interceptor redirige a /login ANTES de
 * que se ejecute este método (por eso acá no hace falta volver a chequear
 * la sesión "a mano" en cada controlador nuevo que se agregue).
 * =============================================================================
 */
@Controller
public class HomeController {

    private final UsuarioRepository usuarioRepository;

    public HomeController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        String correo = (String) session.getAttribute(AuthController.SESSION_USUARIO);

        // Se busca el usuario completo en la base para mostrar su nombre en el panel.
        Usuario usuario = usuarioRepository.findByCorreoPersonal(correo).orElse(null);
        model.addAttribute("usuario", usuario);

        return "home"; // -> templates/home.html
    }
}
