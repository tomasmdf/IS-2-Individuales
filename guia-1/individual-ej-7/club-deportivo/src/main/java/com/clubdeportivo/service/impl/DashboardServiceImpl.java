package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.DashboardDTO;
import com.clubdeportivo.model.enums.MedioPago;
import com.clubdeportivo.service.ControlAccesoService;
import com.clubdeportivo.service.DashboardService;
import com.clubdeportivo.service.FamiliarService;
import com.clubdeportivo.service.PagoCuotaService;
import com.clubdeportivo.service.SocioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ========================================================================================
 * IMPLEMENTACIÓN DEL SERVICIO: DashboardServiceImpl
 * ========================================================================================
 * Centraliza las estadísticas operativas, perimetrales y financieras.
 * ========================================================================================
 */
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final SocioService socioService;
    private final FamiliarService familiarService;
    private final ControlAccesoService controlAccesoService;
    private final PagoCuotaService pagoCuotaService;

    public DashboardServiceImpl(SocioService socioService,
                                FamiliarService familiarService,
                                ControlAccesoService controlAccesoService,
                                PagoCuotaService pagoCuotaService) {
        this.socioService = socioService;
        this.familiarService = familiarService;
        this.controlAccesoService = controlAccesoService;
        this.pagoCuotaService = pagoCuotaService;
    }

    @Override
    public DashboardDTO obtenerMetricasGenerales() {
        DashboardDTO dto = new DashboardDTO();

        long sociosActivos = socioService.contarSociosActivos();
        long familiaresActivos = familiarService.contarFamiliaresActivos();

        dto.setTotalSociosActivos(sociosActivos);
        dto.setTotalFamiliaresActivos(familiaresActivos);
        dto.setTotalPersonasRegistradas(sociosActivos + familiaresActivos);

        dto.setTotalAccesosHoy(controlAccesoService.contarAccesosHoy());
        dto.setEntradasHoy(controlAccesoService.contarEntradasHoy());
        dto.setSalidasHoy(controlAccesoService.contarSalidasHoy());
        dto.setPersonasActualmenteDentro(controlAccesoService.calcularPersonasActualmenteDentro());

        dto.setRecaudacionMesActual(pagoCuotaService.calcularRecaudacionMesActual());
        dto.setRecaudacionEfectivo(pagoCuotaService.calcularRecaudacionPorMedio(MedioPago.EFECTIVO));
        dto.setRecaudacionTransferencia(pagoCuotaService.calcularRecaudacionPorMedio(MedioPago.TRANSFERENCIA));
        dto.setRecaudacionMercadoPago(pagoCuotaService.calcularRecaudacionPorMedio(MedioPago.MERCADO_PAGO));

        dto.setUltimosAccesos(controlAccesoService.obtenerUltimosAccesos());
        dto.setUltimosPagos(pagoCuotaService.listarUltimosDiez());

        return dto;
    }
}
