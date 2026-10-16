package com.clubdeportivo.service;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.PagoCuotaFormDTO;
import com.clubdeportivo.model.enums.MedioPago;

import java.math.BigDecimal;
import java.util.List;

/**
 * ========================================================================================
 * INTERFAZ DE SERVICIO: PagoCuotaService
 * ========================================================================================
 * Define las operaciones financieras para registrar y consultar las cuotas del club deportivo
 * por grupo familiar con diferentes medios de pago (Efectivo, Transferencia, Mercado Pago).
 * ========================================================================================
 */
public interface PagoCuotaService {

    PagoCuotaDTO registrarPago(PagoCuotaFormDTO formDTO);

    List<PagoCuotaDTO> listarTodos();

    List<PagoCuotaDTO> listarUltimosDiez();

    List<PagoCuotaDTO> listarPorSocio(Long socioId);

    PagoCuotaDTO obtenerPorId(Long id);

    PagoCuotaDTO obtenerPorComprobante(String comprobante);

    BigDecimal calcularRecaudacionMesActual();

    BigDecimal calcularRecaudacionPorMedio(MedioPago medioPago);
}
