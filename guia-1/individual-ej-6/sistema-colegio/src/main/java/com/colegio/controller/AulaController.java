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

import com.colegio.dto.AulaRequest;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.service.AulaService;
import com.colegio.service.GradoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/aulas")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService aulaService;
    private final GradoService gradoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("aulas", aulaService.listar());
        return "aulas/listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        if (!model.containsAttribute("aulaRequest")) {
            model.addAttribute("aulaRequest", new AulaRequest(null, null, "", null));
        }
        model.addAttribute("grados", gradoService.listar());
        return "aulas/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        var aula = aulaService.obtener(id);
        model.addAttribute("aulaRequest", new AulaRequest(aula.id(), aula.gradoId(), aula.division(), aula.capacidad()));
        model.addAttribute("grados", gradoService.listar());
        return "aulas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("aulaRequest") AulaRequest request,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("grados", gradoService.listar());
            return "aulas/formulario";
        }
        try {
            if (request.id() == null) {
                aulaService.crear(request);
                redirectAttributes.addFlashAttribute("mensajeExito", "Aula creada correctamente.");
            } else {
                aulaService.actualizar(request.id(), request);
                redirectAttributes.addFlashAttribute("mensajeExito", "Aula actualizada correctamente.");
            }
        } catch (RegistroDuplicadoException ex) {
            bindingResult.rejectValue("division", "duplicado", ex.getMessage());
            model.addAttribute("grados", gradoService.listar());
            return "aulas/formulario";
        }
        return "redirect:/aulas";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        aulaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Aula eliminada.");
        return "redirect:/aulas";
    }
}
