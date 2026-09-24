package com.colegio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.colegio.dto.AlumnoRequest;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.model.enums.Sexo;
import com.colegio.service.AlumnoService;
import com.colegio.service.AulaService;
import com.colegio.service.GradoService;
import com.colegio.service.NotaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * El listado (GET /alumnos) es visible para ADMIN y DOCENTE (útil para
 * consultar el curso), pero el alta/edición/baja quedan reservadas al ADMIN:
 * el propio método valida el rol explícitamente con @PreAuthorize, un
 * control de autorización MÁS FINO que el filtro por URL de SecurityConfig
 * (que sólo exige "ADMIN o DOCENTE" para todo /alumnos/**).
 */
@Controller
@RequestMapping("/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final GradoService gradoService;
    private final AulaService aulaService;
    private final NotaService notaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alumnos", alumnoService.listar());
        return "alumnos/listado";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("alumno", alumnoService.obtener(id));
        model.addAttribute("notas", notaService.listarPorAlumno(id));
        return "alumnos/detalle";
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        if (!model.containsAttribute("alumnoRequest")) {
            model.addAttribute("alumnoRequest", new AlumnoRequest(null, "", "", "", null, null, null, null));
        }
        model.addAttribute("sexos", Sexo.values());
        model.addAttribute("grados", gradoService.listar());
        model.addAttribute("aulas", aulaService.listar());
        return "alumnos/formulario";
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        var alumno = alumnoService.obtener(id);
        model.addAttribute("alumnoRequest", new AlumnoRequest(
                alumno.id(), alumno.nombre(), alumno.apellido(), alumno.dni(), alumno.sexo(),
                alumno.fechaNacimiento(), alumno.gradoId(), alumno.aulaId()));
        model.addAttribute("sexos", Sexo.values());
        model.addAttribute("grados", gradoService.listar());
        model.addAttribute("aulas", aulaService.listar());
        return "alumnos/formulario";
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("alumnoRequest") AlumnoRequest request,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sexos", Sexo.values());
            model.addAttribute("grados", gradoService.listar());
            model.addAttribute("aulas", aulaService.listar());
            return "alumnos/formulario";
        }
        try {
            if (request.id() == null) {
                alumnoService.crear(request);
                redirectAttributes.addFlashAttribute("mensajeExito", "Alumno registrado correctamente.");
            } else {
                alumnoService.actualizar(request.id(), request);
                redirectAttributes.addFlashAttribute("mensajeExito", "Alumno actualizado correctamente.");
            }
        } catch (RegistroDuplicadoException ex) {
            bindingResult.rejectValue("dni", "duplicado", ex.getMessage());
            model.addAttribute("sexos", Sexo.values());
            model.addAttribute("grados", gradoService.listar());
            model.addAttribute("aulas", aulaService.listar());
            return "alumnos/formulario";
        }
        return "redirect:/alumnos";
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        alumnoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Alumno eliminado.");
        return "redirect:/alumnos";
    }
}
