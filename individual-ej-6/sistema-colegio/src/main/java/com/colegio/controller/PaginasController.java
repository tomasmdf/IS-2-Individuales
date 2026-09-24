package com.colegio.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import com.colegio.service.AlumnoService;
import com.colegio.service.DocenteService;
import com.colegio.service.GradoService;
import com.colegio.service.MateriaService;

import lombok.RequiredArgsConstructor;

/**
 * =============================================================================
 * CAPA CONTROLLER (la "C" de MVC)
 * =============================================================================
 * Un @Controller (a diferencia de @RestController) le indica a Spring MVC
 * que el String devuelto por cada método NO es el cuerpo de la respuesta,
 * sino el NOMBRE LÓGICO de una plantilla Thymeleaf dentro de
 * src/main/resources/templates/ (resuelta automáticamente con el prefijo
 * "templates/" y el sufijo ".html" configurados por Spring Boot). El
 * ViewResolver de Thymeleaf combina esa plantilla con el "Model" (los
 * atributos agregados con model.addAttribute) para producir el HTML final.
 *
 * Este controlador atiende las páginas "generales" que no pertenecen a
 * ningún ABM en particular: login, dashboard y la página de acceso denegado.
 * =============================================================================
 */
@Controller
@RequiredArgsConstructor
public class PaginasController {

    private final AlumnoService alumnoService;
    private final DocenteService docenteService;
    private final GradoService gradoService;
    private final MateriaService materiaService;

    /**
     * GET /login: sólo MUESTRA el formulario (plantilla basada en
     * auth-login-basic.html de Sneat). El POST a esta misma URL lo procesa
     * directamente Spring Security (ver security.SecurityConfig,
     * loginProcessingUrl("/login")), sin pasar por este método.
     */
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    /** Panel principal: pequeño resumen numérico para orientar al usuario recién logueado. */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("cantidadAlumnos", alumnoService.listar().size());
        model.addAttribute("cantidadDocentes", docenteService.listar().size());
        model.addAttribute("cantidadGrados", gradoService.listar().size());
        model.addAttribute("cantidadMaterias", materiaService.listar().size());
        return "dashboard";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "error/acceso-denegado";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String manejarAccesoDenegado() {
        return "redirect:/acceso-denegado";
    }
}
