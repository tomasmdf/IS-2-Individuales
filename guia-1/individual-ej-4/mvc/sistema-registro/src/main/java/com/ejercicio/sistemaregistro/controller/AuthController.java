package com.ejercicio.sistemaregistro.controller;

import com.ejercicio.sistemaregistro.dto.LoginDTO;
import com.ejercicio.sistemaregistro.dto.RegistroDTO;
import com.ejercicio.sistemaregistro.model.Usuario;
import com.ejercicio.sistemaregistro.service.ResultadoLogin;
import com.ejercicio.sistemaregistro.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * =============================================================================
 * "AuthController" — CAPA CONTROLADOR (C de MVC)
 * =============================================================================
 * @Controller : anotación de Spring MVC que marca esta clase como un
 *            controlador "clásico" (a diferencia de @RestController, que
 *            devolvería JSON). Los métodos devuelven un String con el
 *            NOMBRE LÓGICO de la vista Thymeleaf a renderizar (por ejemplo,
 *            "login" -> resuelve a /templates/login.html).
 *
 * Responsabilidades del controlador (y SOLO estas, siguiendo el patrón MVC):
 *   1) Recibir la petición HTTP (GET/POST) y sus parámetros/formulario.
 *   2) Delegar toda la lógica de negocio a la capa de SERVICIO
 *      (UsuarioService) — el controlador NO debe contener reglas de negocio.
 *   3) Preparar el "Model" (datos) que se le pasan a la Vista Thymeleaf.
 *   4) Decidir a qué vista redirigir/renderizar según el resultado.
 *
 * Manejo de sesión: como este ejercicio no usa Spring Security completo,
 * la sesión del usuario autenticado se guarda "a mano" en el HttpSession
 * bajo el atributo "USUARIO_SESION". El SesionInterceptor (config/) revisa
 * ese atributo para proteger las rutas privadas (como /home).
 * =============================================================================
 */
@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Nombre del atributo de sesión donde se guarda el usuario logueado. */
    public static final String SESSION_USUARIO = "USUARIO_SESION";

    // ==================== LOGIN ====================

    /**
     * Muestra el formulario de login (GET /login).
     * @ModelAttribute crea automáticamente un LoginDTO vacío para el
     * data-binding con el formulario Thymeleaf (th:object="${loginDTO}").
     */
    @GetMapping("/login")
    public String mostrarLogin(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login"; // -> templates/login.html
    }

    /**
     * Procesa el envío del formulario de login (POST /login).
     *
     * @Valid dispara las validaciones del LoginDTO (@NotBlank en los campos);
     * BindingResult recoge los errores de validación SIN lanzar excepción,
     * para poder volver a mostrar el formulario con los mensajes de error.
     *
     * Según el resultado de usuarioService.autenticar(...):
     *   - USUARIO_NO_REGISTRADO -> se redirige a /registro (regla del enunciado:
     *       "si el usuario no está registrado, se le solicita registrarse").
     *   - USUARIO_BLOQUEADO     -> se informa que la cuenta está bloqueada.
     *   - CLAVE_INCORRECTA      -> se informa el error, sin revelar cuántos
     *       intentos le quedan por motivos de seguridad (opcionalmente se
     *       podría mostrar).
     *   - LOGIN_EXITOSO         -> se guarda el usuario en sesión y se
     *       redirige al panel principal (/home).
     */
    @PostMapping("/login")
    public String procesarLogin(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        ResultadoLogin resultado = usuarioService.autenticar(loginDTO.getCorreoPersonal(), loginDTO.getClave());

        switch (resultado) {
            case USUARIO_NO_REGISTRADO -> {
                // Regla del enunciado: si no está registrado, se lo invita a registrarse.
                model.addAttribute("mensaje", "El usuario no está registrado. Por favor, complete el registro.");
                model.addAttribute("registroDTO", new RegistroDTO());
                return "registro";
            }
            case USUARIO_BLOQUEADO -> {
                model.addAttribute("error", "Su usuario se encuentra bloqueado por superar los 3 intentos fallidos. Contacte al administrador.");
                return "login";
            }
            case CLAVE_INCORRECTA -> {
                model.addAttribute("error", "Usuario o clave incorrectos.");
                return "login";
            }
            case LOGIN_EXITOSO -> {
                // Se guarda el correo del usuario autenticado en la sesión HTTP.
                session.setAttribute(SESSION_USUARIO, loginDTO.getCorreoPersonal());
                return "redirect:/home";
            }
            default -> {
                return "login";
            }
        }
    }

    // ==================== REGISTRO ====================

    /** Muestra el formulario de registro (GET /registro). */
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registroDTO", new RegistroDTO());
        return "registro"; // -> templates/registro.html
    }

    /**
     * Procesa el envío del formulario de registro (POST /registro).
     * Valida los datos personales (Nombre, Apellido, Documento, Fecha de
     * Nacimiento, Correo Personal) y crea el usuario a través del servicio.
     */
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("registroDTO") RegistroDTO registroDTO,
                                    BindingResult bindingResult,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            return "registro";
        }

        try {
            Usuario nuevoUsuario = usuarioService.registrar(registroDTO);
            model.addAttribute("mensaje", "Usuario " + nuevoUsuario.getCorreoPersonal() + " registrado con éxito. Ya puede iniciar sesión.");
            model.addAttribute("loginDTO", new LoginDTO());
            return "login";
        } catch (IllegalArgumentException e) {
            // Errores de negocio (correo/documento duplicado, claves no coinciden, etc.)
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    // ==================== LOGOUT ====================

    /** Cierra la sesión del usuario y lo vuelve a la pantalla de login. */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ==================== RAÍZ ====================

    /** La raíz del sitio redirige directamente al login. */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }
}
