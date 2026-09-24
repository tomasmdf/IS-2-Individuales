package com.clubdeportivo.controller;

import com.clubdeportivo.dto.DashboardDTO;
import com.clubdeportivo.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ========================================================================================
 * CONTROLADOR MVC: DashboardController
 * ========================================================================================
 * Atiende las solicitudes a la página de inicio y expone las métricas agregadas al modelo.
 * ========================================================================================
 */
@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/", "/dashboard"})
    public String index(Model model) {
        DashboardDTO metricas = dashboardService.obtenerMetricasGenerales();
        model.addAttribute("dashboard", metricas);
        model.addAttribute("moduloActivo", "dashboard");
        return "dashboard/index";
    }
}
