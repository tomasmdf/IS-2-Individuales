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

import com.colegio.dto.DocenteRequest;
import com.colegio.exception.RegistroDuplicadoException;
import com.colegio.model.enums.Sexo;
import com.colegio.service.DocenteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ABM de Docentes, accesible sólo por ADMIN (restringido en SecurityConfig).
 * El alta ("registrar") dispara automáticamente el correo de bienvenida
 * (ver DocenteService.registrar -> event.DocenteRegistradoEvent).
 */
@Controller
@RequestMapping("/docentes")
@RequiredArgsConstructor
public class DocenteController {

    private final DocenteService docenteService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("docentes", docenteService.listar());
        return "docentes/listado";
    }

    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        if (!model.containsAttribute("docenteRequest")) {
            model.addAttribute("docenteRequest", new DocenteRequest(null, "", "", null, null, ""));
        }
        model.addAttribute("sexos", Sexo.values());
        return "docentes/formulario";
    }

    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Long id, Model model) {
        var docente = docenteService.obtener(id);
        model.addAttribute("docenteRequest", new DocenteRequest(
                docente.id(), docente.nombre(), docente.apellido(), docente.sexo(), docente.fechaNacimiento(), docente.correo()));
        model.addAttribute("sexos", Sexo.values());
        return "docentes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("docenteRequest") DocenteRequest request,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("sexos", Sexo.values());
            return "docentes/formulario";
        }
        try {
            if (request.id() == null) {
                docenteService.registrar(request);
                redirectAttributes.addFlashAttribute("mensajeExito",
                        "Docente registrado. Se envió un correo de bienvenida a " + request.correo() + ".");
            } else {
                docenteService.actualizar(request.id(), request);
                redirectAttributes.addFlashAttribute("mensajeExito", "Docente actualizado correctamente.");
            }
        } catch (RegistroDuplicadoException ex) {
            bindingResult.rejectValue("correo", "duplicado", ex.getMessage());
            model.addAttribute("sexos", Sexo.values());
            return "docentes/formulario";
        }
        return "redirect:/docentes";
    }

    /** Baja lógica (activo/inactivo) en lugar de borrado físico: preserva el historial de notas cargadas. */
    @PostMapping("/{id}/alternar-activo")
    public String alternarActivo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        docenteService.alternarActivo(id);
        redirectAttributes.addFlashAttribute("mensajeExito", "Estado del docente actualizado.");
        return "redirect:/docentes";
    }

    /**
     * Genera una contraseña nueva para el docente y se la reenvía por correo.
     * Se usa cuando el docente perdió u nunca recibió el correo de bienvenida
     * original: como la contraseña se guarda con hash (irreversible), la
     * única opción es reemplazarla por una nueva, nunca "recuperar" la vieja.
     */
    @PostMapping("/{id}/restablecer-password")
    public String restablecerPassword(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        docenteService.restablecerPassword(id);
        redirectAttributes.addFlashAttribute("mensajeExito",
                "Se generó una nueva contraseña y se envió por correo al docente.");
        return "redirect:/docentes";
    }
}
