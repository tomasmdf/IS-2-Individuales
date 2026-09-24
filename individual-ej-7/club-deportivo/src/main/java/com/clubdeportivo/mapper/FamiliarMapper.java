package com.clubdeportivo.mapper;

import com.clubdeportivo.dto.FamiliarDTO;
import com.clubdeportivo.dto.FamiliarFormDTO;
import com.clubdeportivo.model.Familiar;
import com.clubdeportivo.model.Socio;
import org.springframework.stereotype.Component;

/**
 * ========================================================================================
 * COMPONENTE MAPPER: FamiliarMapper
 * ========================================================================================
 * Mapeo bidireccional entre la entidad Familiar y sus representaciones DTO.
 * ========================================================================================
 */
@Component
public class FamiliarMapper {

    public FamiliarDTO toDTO(Familiar familiar) {
        if (familiar == null) {
            return null;
        }

        FamiliarDTO dto = new FamiliarDTO();
        dto.setId(familiar.getId());
        if (familiar.getSocio() != null) {
            dto.setSocioId(familiar.getSocio().getId());
            dto.setSocioNombreCompleto(familiar.getSocio().getNombreCompleto());
            dto.setSocioDni(familiar.getSocio().getDni());
        }
        dto.setDni(familiar.getDni());
        dto.setNombre(familiar.getNombre());
        dto.setApellido(familiar.getApellido());
        dto.setNombreCompleto(familiar.getNombreCompleto());
        dto.setParentesco(familiar.getParentesco());
        dto.setFechaNacimiento(familiar.getFechaNacimiento());
        dto.setFotoRostro(familiar.getFotoRostro());
        dto.setActivo(familiar.isActivo());

        return dto;
    }

    public Familiar toEntity(FamiliarFormDTO formDTO, Socio socio, String nombreArchivoFoto) {
        if (formDTO == null) {
            return null;
        }

        Familiar familiar = new Familiar();
        familiar.setId(formDTO.getId());
        familiar.setSocio(socio);
        familiar.setDni(formDTO.getDni());
        familiar.setNombre(formDTO.getNombre());
        familiar.setApellido(formDTO.getApellido());
        familiar.setParentesco(formDTO.getParentesco());
        familiar.setFechaNacimiento(formDTO.getFechaNacimiento());
        familiar.setActivo(formDTO.isActivo());
        familiar.setFotoRostro(nombreArchivoFoto);

        return familiar;
    }

    public FamiliarFormDTO toFormDTO(Familiar familiar) {
        if (familiar == null) {
            return null;
        }

        FamiliarFormDTO form = new FamiliarFormDTO();
        form.setId(familiar.getId());
        if (familiar.getSocio() != null) {
            form.setSocioId(familiar.getSocio().getId());
            form.setSocioNombreCompleto(familiar.getSocio().getNombreCompleto());
        }
        form.setDni(familiar.getDni());
        form.setNombre(familiar.getNombre());
        form.setApellido(familiar.getApellido());
        form.setParentesco(familiar.getParentesco());
        form.setFechaNacimiento(familiar.getFechaNacimiento());
        form.setFotoRostroActual(familiar.getFotoRostro());
        form.setActivo(familiar.isActivo());

        return form;
    }

    public void updateEntityFromForm(Familiar familiar, FamiliarFormDTO formDTO, String nuevoArchivoFoto) {
        familiar.setNombre(formDTO.getNombre());
        familiar.setApellido(formDTO.getApellido());
        familiar.setParentesco(formDTO.getParentesco());
        familiar.setFechaNacimiento(formDTO.getFechaNacimiento());
        familiar.setActivo(formDTO.isActivo());

        if (nuevoArchivoFoto != null && !nuevoArchivoFoto.isBlank()) {
            familiar.setFotoRostro(nuevoArchivoFoto);
        }
    }
}
