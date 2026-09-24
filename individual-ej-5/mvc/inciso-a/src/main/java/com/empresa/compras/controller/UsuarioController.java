package com.empresa.compras.controller;

import com.empresa.compras.dto.UsuarioDTO;
import com.empresa.compras.enums.RolUsuario;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ABM de Usuarios. Restringido al rol ADMIN (ver SecurityConfig:
 * "/usuarios/**" -> hasRole("ADMIN")).
 */
@Controller
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuarios/list";
    }

    @GetMapping("/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("usuario", UsuarioDTO.builder().activo(true).build());
        model.addAttribute("roles", RolUsuario.values());
        return "usuarios/form";
    }

    @GetMapping("/{id}/editar")
    public String editarFormulario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscarPorId(id));
        model.addAttribute("roles", RolUsuario.values());
        return "usuarios/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuario") UsuarioDTO dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", RolUsuario.values());
            return "usuarios/form";
        }
        try {
            usuarioService.guardar(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario guardado correctamente");
            return "redirect:/usuarios";
        } catch (BusinessException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("roles", RolUsuario.values());
            return "usuarios/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Usuario dado de baja correctamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
