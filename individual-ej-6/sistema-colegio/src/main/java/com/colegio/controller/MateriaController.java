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

import com.colegio.dto.MateriaRequest;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.service.MateriaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final MateriaService materiaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("materias", materiaService.listar());
        return "materias/listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        if (!model.containsAttribute("materiaRequest")) {
            model.addAttribute("materiaRequest", new MateriaRequest(null, "", ""));
        }
        return "materias/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        var materia = materiaService.obtener(id);
        model.addAttribute("materiaRequest", new MateriaRequest(materia.id(), materia.nombre(), materia.descripcion()));
        return "materias/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("materiaRequest") MateriaRequest request,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "materias/formulario";
        }
        try {
            if (request.id() == null) {
                materiaService.crear(request);
                redirectAttributes.addFlashAttribute("mensajeExito", "Materia creada correctamente.");
            } else {
                materiaService.actualizar(request.id(), request);
                redirectAttributes.addFlashAttribute("mensajeExito", "Materia actualizada correctamente.");
            }
        } catch (RegistroDuplicadoException ex) {
            bindingResult.rejectValue("nombre", "duplicado", ex.getMessage());
            return "materias/formulario";
        }
        return "redirect:/materias";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        materiaService.eliminar(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Materia eliminada.");
        return "redirect:/materias";
    }
}
