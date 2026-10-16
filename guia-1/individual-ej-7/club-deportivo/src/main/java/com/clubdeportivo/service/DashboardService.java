package com.clubdeportivo.service;

import com.clubdeportivo.dto.DashboardDTO;

/**
 * ========================================================================================
 * INTERFAZ DE SERVICIO: DashboardService
 * ========================================================================================
 * Proveedor de métricas agregadas e indicadores operativos en tiempo real para el Dashboard.
 * ========================================================================================
 */
public interface DashboardService {

    DashboardDTO obtenerMetricasGenerales();
}
