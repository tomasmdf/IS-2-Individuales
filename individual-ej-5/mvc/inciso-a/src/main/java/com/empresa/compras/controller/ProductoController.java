package com.empresa.compras.controller;

import com.empresa.compras.dto.ProductoDTO;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.service.CategoriaService;
import com.empresa.compras.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "productos/list";
    }

    @GetMapping("/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("producto", ProductoDTO.builder().activo(true).stock(0).stockMinimo(0).build());
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "productos/form";
    }

    @GetMapping("/{id}/editar")
    public String editarFormulario(@PathVariable Long id, Model model) {
        model.addAttribute("producto", productoService.buscarPorId(id));
        model.addAttribute("categorias", categoriaService.listarTodas());
        return "productos/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") ProductoDTO dto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "productos/form";
        }
        try {
            productoService.guardar(dto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto guardado correctamente");
            return "redirect:/productos";
        } catch (BusinessException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("categorias", categoriaService.listarTodas());
            return "productos/form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminar(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Producto dado de baja correctamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/productos";
    }
}
