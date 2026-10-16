package com.empresa.compras.controller;

import com.empresa.compras.dto.CategoriaDTO;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador (capa CONTROLLER del patron MVC) para el ABM de Categorias.
 * Responsabilidad UNICA de esta capa: recibir la peticion HTTP, delegar en
 * el Service (que valida y ejecuta la regla de negocio) y decidir que vista
 * Thymeleaf renderizar o a que URL redirigir. No contiene logica de negocio.
 */
@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "categorias/list";
    }

    @GetMapping("/nueva")
    public String nuevoFormulario(Model model) {
        model.addAttribute("categoria", new CategoriaDTO());
        return "categorias/form";
    }

    @GetMapping("/{id}/editar")
    public String editarFormulario(@PathVariable Long id, Model model) {
        model.addAttribute("categoria", categoriaService.buscarPorId(id));
        return "categorias/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("categoria") CategoriaDTO dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            return "categorias/form";
        }
        try {
            categoriaService.guardar(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Categoria guardada correctamente");
            return "redirect:/categorias";
        } catch (BusinessException e) {
            model.addAttribute("mensajeError", e.getMessage());
            return "categorias/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Categoria eliminada correctamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/categorias";
    }
}
