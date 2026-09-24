package com.empresa.compras.controller;

import com.empresa.compras.dto.OrdenCompraDTO;
import com.empresa.compras.dto.ProductoDTO;
import com.empresa.compras.enums.EstadoOrdenCompra;
import com.empresa.compras.service.OrdenCompraService;
import com.empresa.compras.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Pantalla principal luego del login: resumen de productos con stock bajo
 * y ultimas ordenes de compra pendientes, para dar contexto rapido al
 * usuario del sistema.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ProductoService productoService;
    private final OrdenCompraService ordenCompraService;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        List<ProductoDTO> productos = productoService.listarActivos();

        long totalProductos = productos.size();
        List<ProductoDTO> stockBajo = productos.stream()
                .filter(p -> p.getStock() <= p.getStockMinimo())
                .collect(Collectors.toList());

        List<OrdenCompraDTO> ordenes = ordenCompraService.listarTodas();
        long ordenesPendientes = ordenes.stream()
                .filter(o -> o.getEstado() == EstadoOrdenCompra.PENDIENTE)
                .count();

        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("productosStockBajo", stockBajo);
        model.addAttribute("cantidadStockBajo", stockBajo.size());
        model.addAttribute("ordenesPendientes", ordenesPendientes);
        model.addAttribute("ultimasOrdenes", ordenes.stream().limit(5).collect(Collectors.toList()));

        return "dashboard/index";
    }
}
