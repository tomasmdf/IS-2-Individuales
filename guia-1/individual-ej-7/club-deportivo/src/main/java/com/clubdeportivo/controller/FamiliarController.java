package com.clubdeportivo.controller;

import com.clubdeportivo.dto.FamiliarFormDTO;
import com.clubdeportivo.dto.SocioDTO;
import com.clubdeportivo.model.enums.Parentesco;
import com.clubdeportivo.service.FamiliarService;
import com.clubdeportivo.service.SocioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ========================================================================================
 * CONTROLADOR MVC: FamiliarController
 * ========================================================================================
 * Gestiona el alta, edición y baja de miembros del grupo familiar dependientes de un socio titular.
 * Permite registrar la imagen con el rostro de cada dependiente.
 * ========================================================================================
 */
@Controller
@RequestMapping("/familiares")
public class FamiliarController {

    private final FamiliarService familiarService;
    private final SocioService socioService;

    public FamiliarController(FamiliarService familiarService, SocioService socioService) {
        this.familiarService = familiarService;
        this.socioService = socioService;
    }

    /**
     * Muestra el formulario para incorporar un integrante al grupo familiar de un socio.
     */
    @GetMapping("/nuevo/{socioId}")
    public String nuevoFamiliar(@PathVariable("socioId") Long socioId, Model model) {
        SocioDTO socio = socioService.obtenerPorId(socioId);

        FamiliarFormDTO form = new FamiliarFormDTO();
        form.setSocioId(socioId);
        form.setSocioNombreCompleto(socio.getNombreCompleto());

        model.addAttribute("familiarForm", form);
        model.addAttribute("socio", socio);
        model.addAttribute("parentescos", Parentesco.values());
        model.addAttribute("esEdicion", false);
        model.addAttribute("moduloActivo", "socios");
        return "familiares/form";
    }

    /**
     * Procesa el guardado del familiar con su correspondiente imagen facial.
     */
    @PostMapping("/guardar")
    public String guardarFamiliar(
            @Valid @ModelAttribute("familiarForm") FamiliarFormDTO formDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            SocioDTO socio = socioService.obtenerPorId(formDTO.getSocioId());
            model.addAttribute("socio", socio);
            model.addAttribute("parentescos", Parentesco.values());
            model.addAttribute("esEdicion", formDTO.getId() != null);
            model.addAttribute("moduloActivo", "socios");
            return "familiares/form";
        }

        try {
            if (formDTO.getId() == null) {
                familiarService.registrarFamiliar(formDTO);
                redirectAttributes.addFlashAttribute("mensajeExito", "Integrante familiar agregado correctamente.");
            } else {
                familiarService.actualizarFamiliar(formDTO);
                redirectAttributes.addFlashAttribute("mensajeExito", "Datos del familiar actualizados correctamente.");
            }
            return "redirect:/socios/detalle/" + formDTO.getSocioId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            SocioDTO socio = socioService.obtenerPorId(formDTO.getSocioId());
            model.addAttribute("socio", socio);
            model.addAttribute("parentescos", Parentesco.values());
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("esEdicion", formDTO.getId() != null);
            model.addAttribute("moduloActivo", "socios");
            return "familiares/form";
        }
    }

    /**
     * Muestra el formulario para modificar datos de un familiar existente.
     */
    @GetMapping("/editar/{id}")
    public String editarFamiliar(@PathVariable("id") Long id, Model model) {
        FamiliarFormDTO form = familiarService.obtenerFormularioPorId(id);
        SocioDTO socio = socioService.obtenerPorId(form.getSocioId());

        model.addAttribute("familiarForm", form);
        model.addAttribute("socio", socio);
        model.addAttribute("parentescos", Parentesco.values());
        model.addAttribute("esEdicion", true);
        model.addAttribute("moduloActivo", "socios");
        return "familiares/form";
    }

    /**
     * Da de baja a un integrante del grupo familiar.
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarFamiliar(@PathVariable("id") Long id,
                                   @RequestParam("socioId") Long socioId,
                                   RedirectAttributes redirectAttributes) {
        try {
            familiarService.darDeBaja(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "El familiar ha sido dado de baja.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al dar de baja al familiar: " + e.getMessage());
        }
        return "redirect:/socios/detalle/" + socioId;
    }
}
