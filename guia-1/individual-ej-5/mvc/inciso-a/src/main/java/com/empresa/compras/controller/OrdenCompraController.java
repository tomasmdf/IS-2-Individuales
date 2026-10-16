package com.empresa.compras.controller;

import com.empresa.compras.dto.OrdenCompraDTO;
import com.empresa.compras.exception.BusinessException;
import com.empresa.compras.service.OrdenCompraService;
import com.empresa.compras.service.ProductoService;
import com.empresa.compras.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/**
 * ============================================================================
 * OrdenCompraController
 * ============================================================================
 * Expone el flujo completo de compras a proveedores:
 *   GET  /ordenes-compra              -> listado
 *   GET  /ordenes-compra/nueva        -> formulario maestro-detalle de alta
 *   GET  /ordenes-compra/{id}         -> detalle de una orden (solo lectura)
 *   POST /ordenes-compra/guardar      -> registra la orden (estado PENDIENTE)
 *   POST /ordenes-compra/{id}/recibir -> confirma la recepcion -> actualiza stock
 *   POST /ordenes-compra/{id}/anular  -> cancela una orden pendiente
 *
 * El usuario que registra la orden se obtiene del "Authentication" inyectado
 * por Spring Security (usuario actualmente logueado), no de un campo oculto
 * del formulario, para evitar que se falsifique.
 * ============================================================================
 */
@Controller
@RequestMapping("/ordenes-compra")
@RequiredArgsConstructor
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;
    private final ProveedorService proveedorService;
    private final ProductoService productoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ordenes", ordenCompraService.listarTodas());
        return "ordenes/list";
    }

    @GetMapping("/nueva")
    public String nuevoFormulario(Model model) {
        OrdenCompraDTO dto = OrdenCompraDTO.builder().fecha(LocalDate.now()).build();
        model.addAttribute("orden", dto);
        model.addAttribute("proveedores", proveedorService.listarActivos());
        model.addAttribute("productos", productoService.listarActivos());
        return "ordenes/form";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        model.addAttribute("orden", ordenCompraService.buscarPorId(id));
        return "ordenes/detalle";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("orden") OrdenCompraDTO dto,
                           BindingResult result,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("proveedores", proveedorService.listarActivos());
            model.addAttribute("productos", productoService.listarActivos());
            return "ordenes/form";
        }
        try {
            String username = authentication.getName();
            OrdenCompraDTO creada = ordenCompraService.registrar(dto, username);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Orden de compra " + creada.getNumero() + " registrada correctamente");
            return "redirect:/ordenes-compra";
        } catch (BusinessException e) {
            model.addAttribute("mensajeError", e.getMessage());
            model.addAttribute("proveedores", proveedorService.listarActivos());
            model.addAttribute("productos", productoService.listarActivos());
            return "ordenes/form";
        }
    }

    @PostMapping("/{id}/recibir")
    public String recibir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            OrdenCompraDTO orden = ordenCompraService.recibirOrden(id);
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Orden " + orden.getNumero() + " recibida. El stock de los productos fue actualizado.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/ordenes-compra/" + id;
    }

    @PostMapping("/{id}/anular")
    public String anular(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ordenCompraService.anularOrden(id);
            redirectAttributes.addFlashAttribute("mensajeExito", "Orden anulada correctamente");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/ordenes-compra/" + id;
    }
}
