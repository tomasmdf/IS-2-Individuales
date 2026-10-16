package com.colegio.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.colegio.dto.CambiarPasswordRequest;
import com.colegio.exception.PasswordActualIncorrectaException;
import com.colegio.security.DocenteUserDetails;
import com.colegio.service.DocenteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Área "Mi cuenta": cada docente ve su propio perfil y puede cambiar su
 * contraseña (requisito del enunciado). No requiere rol ADMIN: cualquier
 * usuario autenticado accede sólo a SUS PROPIOS datos.
 *
 * @AuthenticationPrincipal inyecta directamente el DocenteUserDetails del
 * usuario logueado (lo arma Spring Security a partir de la sesión HTTP),
 * evitando tener que leer manualmente el SecurityContext en el Controller.
 */
@Controller
@RequiredArgsConstructor
public class CuentaController {

    private final DocenteService docenteService;

    @GetMapping("/cuenta")
    public String miCuenta(@AuthenticationPrincipal DocenteUserDetails principal, Model model) {
        model.addAttribute("docente", principal.getDocente());
        return "cuenta/perfil";
    }

    @GetMapping("/cuenta/cambiar-password")
    public String formularioCambiarPassword(Model model) {
        if (!model.containsAttribute("cambiarPasswordRequest")) {
            model.addAttribute("cambiarPasswordRequest", new CambiarPasswordRequest("", "", ""));
        }
        return "cuenta/cambiar-password";
    }

    @PostMapping("/cuenta/cambiar-password")
    public String cambiarPassword(@Valid @ModelAttribute("cambiarPasswordRequest") CambiarPasswordRequest request,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal DocenteUserDetails principal,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (!request.coinciden()) {
            bindingResult.rejectValue("passwordConfirmacion", "noCoincide", "Las contraseñas nuevas no coinciden");
        }
        if (bindingResult.hasErrors()) {
            return "cuenta/cambiar-password";
        }

        try {
            docenteService.cambiarPassword(principal.getUsername(), request);
        } catch (PasswordActualIncorrectaException ex) {
            bindingResult.rejectValue("passwordActual", "incorrecta", ex.getMessage());
            return "cuenta/cambiar-password";
        }

        redirectAttributes.addFlashAttribute("mensajeExito", "Contraseña actualizada correctamente.");
        return "redirect:/cuenta";
    }
}
