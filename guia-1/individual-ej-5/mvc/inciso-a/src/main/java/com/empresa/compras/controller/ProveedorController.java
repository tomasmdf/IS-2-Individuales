package com.empresa.compras.controller;

import com.empresa.compras.dto.ProveedorDTO;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorService.listarTodos());
        return "proveedores/list";
    }

    @GetMapping("/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("proveedor", ProveedorDTO.builder().activo(true).build());
        return "proveedores/form";
    }

    @GetMapping("/{id}/editar")
    public String editarFormulario(@PathVariable Long id, Model model) {
        model.addAttribute("proveedor", proveedorService.buscarPorId(id));
        return "proveedores/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("proveedor") ProveedorDTO dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            return "proveedores/form";
        }
        try {
            proveedorService.guardar(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Proveedor guardado correctamente");
            return "redirect:/proveedores";
        } catch (BusinessException e) {
            model.addAttribute("mensajeError", e.getMessage());
            return "proveedores/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            proveedorService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Proveedor eliminado / dado de baja correctamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/proveedores";
    }
}
