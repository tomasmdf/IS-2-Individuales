package com.clubdeportivo.mapper;

import com.clubdeportivo.dto.RegistroAccesoDTO;
import com.clubdeportivo.model.RegistroAcceso;
import org.springframework.stereotype.Component;

/**
 * ========================================================================================
 * COMPONENTE MAPPER: RegistroAccesoMapper
 * ========================================================================================
 * Convierte registros de auditoría de acceso perimetral a DTOs de presentación.
 * ========================================================================================
 */
@Component
public class RegistroAccesoMapper {

    public RegistroAccesoDTO toDTO(RegistroAcceso registro) {
        if (registro == null) {
            return null;
        }

        RegistroAccesoDTO dto = new RegistroAccesoDTO();
        dto.setId(registro.getId());
        dto.setFechaHora(registro.getFechaHora());
        dto.setTipoAcceso(registro.getTipoAcceso());
        dto.setDniPersona(registro.getDniPersona());
        dto.setNombreCompletoPersona(registro.getNombreCompletoPersona());
        dto.setFotoRostro(registro.getFotoRostro());
        dto.setEsSocioTitular(registro.isEsSocioTitular());
        dto.setSocioId(registro.getSocioId());
        dto.setFamiliarId(registro.getFamiliarId());
        dto.setEstadoCuotaMomento(registro.getEstadoCuotaMomento());
        dto.setPuntoAcceso(registro.getPuntoAcceso());
        dto.setObservaciones(registro.getObservaciones());

        return dto;
    }
}
