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

import com.colegio.dto.AsignacionRequest;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.service.AsignacionService;
import com.colegio.service.AulaService;
import com.colegio.service.DocenteService;
import com.colegio.service.MateriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** ABM de la "carga horaria": qué docente dicta qué materia en qué aula. Sólo ADMIN. */
@Controller
@RequestMapping("/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final DocenteService docenteService;
    private final MateriaService materiaService;
    private final AulaService aulaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("asignaciones", asignacionService.listar());
        return "asignaciones/listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        if (!model.containsAttribute("asignacionRequest")) {
            model.addAttribute("asignacionRequest", new AsignacionRequest(null, null, null, null));
        }
        cargarCombos(model);
        return "asignaciones/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("asignacionRequest") AsignacionRequest request,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarCombos(model);
            return "asignaciones/formulario";
        }
        try {
            asignacionService.crear(request);
            redirectAttributes.addFlashAttribute("mensajeExito", "Asignación creada correctamente.");
        } catch (RegistroDuplicadoException ex) {
            bindingResult.reject("duplicado", ex.getMessage());
            cargarCombos(model);
            return "asignaciones/formulario";
        }
        return "redirect:/asignaciones";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        asignacionService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Asignación eliminada.");
        return "redirect:/asignaciones";
    }

    private void cargarCombos(Model model) {
        model.addAttribute("docentes", docenteService.listar());
        model.addAttribute("materias", materiaService.listar());
        model.addAttribute("aulas", aulaService.listar());
    }
}
