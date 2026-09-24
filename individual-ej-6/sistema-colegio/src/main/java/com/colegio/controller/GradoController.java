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

import com.colegio.dto.GradoRequest;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.service.AulaService;
import com.colegio.service.GradoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/grados")
@RequiredArgsConstructor
public class GradoController {

    private final GradoService gradoService;
    private final AulaService aulaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("grados", gradoService.listar());
        return "grados/listado";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("grado", gradoService.obtener(id));
        model.addAttribute("aulas", aulaService.listarPorGrado(id));
        return "grados/detalle";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        if (!model.containsAttribute("gradoRequest")) {
            model.addAttribute("gradoRequest", new GradoRequest(null, "", ""));
        }
        return "grados/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        var grado = gradoService.obtener(id);
        model.addAttribute("gradoRequest", new GradoRequest(grado.id(), grado.nombre(), grado.nivel()));
        return "grados/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("gradoRequest") GradoRequest request,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "grados/formulario";
        }
        try {
            if (request.id() == null) {
                gradoService.crear(request);
                redirectAttributes.addFlashAttribute("mensajeExito", "Grado creado correctamente.");
            } else {
                gradoService.actualizar(request.id(), request);
                redirectAttributes.addFlashAttribute("mensajeExito", "Grado actualizado correctamente.");
            }
        } catch (RegistroDuplicadoException ex) {
            bindingResult.rejectValue("nombre", "duplicado", ex.getMessage());
            return "grados/formulario";
        }
        return "redirect:/grados";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        gradoService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Grado eliminado.");
        return "redirect:/grados";
    }
}
