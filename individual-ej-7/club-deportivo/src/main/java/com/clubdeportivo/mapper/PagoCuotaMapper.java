package com.clubdeportivo.mapper;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.PagoCuotaFormDTO;
import com.clubdeportivo.model.PagoCuota;
import com.clubdeportivo.model.Socio;
import org.springframework.stereotype.Component;

/**
 * ========================================================================================
 * COMPONENTE MAPPER: PagoCuotaMapper
 * ========================================================================================
 * Mapeo entre entidades PagoCuota y sus respectivos DTOs de presentación y captura de datos.
 * ========================================================================================
 */
@Component
public class PagoCuotaMapper {

    public PagoCuotaDTO toDTO(PagoCuota pago) {
        if (pago == null) {
            return null;
        }

        PagoCuotaDTO dto = new PagoCuotaDTO();
        dto.setId(pago.getId());
        if (pago.getSocio() != null) {
            dto.setSocioId(pago.getSocio().getId());
            dto.setSocioNombreCompleto(pago.getSocio().getNombreCompleto());
            dto.setSocioDni(pago.getSocio().getDni());
            dto.setSocioNumero(pago.getSocio().getNumeroSocio());
        }
        dto.setPeriodoMes(pago.getPeriodoMes());
        dto.setPeriodoAnio(pago.getPeriodoAnio());
        dto.setMonto(pago.getMonto());
        dto.setFechaPago(pago.getFechaPago());
        dto.setMedioPago(pago.getMedioPago());
        dto.setNumeroComprobante(pago.getNumeroComprobante());
        dto.setObservaciones(pago.getObservaciones());
        dto.setCreadoPor(pago.getCreadoPor());

        return dto;
    }

    public PagoCuota toEntity(PagoCuotaFormDTO formDTO, Socio socio, String numeroComprobante) {
        if (formDTO == null) {
            return null;
        }

        PagoCuota pago = new PagoCuota();
        pago.setSocio(socio);
        pago.setPeriodoMes(formDTO.getPeriodoMes());
        pago.setPeriodoAnio(formDTO.getPeriodoAnio());
        pago.setMonto(formDTO.getMonto());
        pago.setMedioPago(formDTO.getMedioPago());
        pago.setNumeroComprobante(numeroComprobante);
        pago.setObservaciones(formDTO.getObservaciones());

        return pago;
    }
}
