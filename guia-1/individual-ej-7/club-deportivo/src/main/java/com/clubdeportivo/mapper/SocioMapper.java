package com.clubdeportivo.mapper;

import com.clubdeportivo.dto.SocioDTO;
import com.clubdeportivo.dto.SocioFormDTO;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.model.enums.EstadoCuota;
import org.springframework.stereotype.Component;

/**
 * ========================================================================================
 * COMPONENTE MAPPER: SocioMapper
 * ========================================================================================
 * Realiza la transformación bidireccional entre la entidad persistente Socio y sus DTOs.
 *
 * Anotaciones utilizadas:
 * - @Component: Registra la clase como un Bean administrado en el contenedor de Spring,
 *   permitiendo su inyección de dependencias (@Autowired o por constructor) en los servicios.
 * ========================================================================================
 */
@Component
public class SocioMapper {

    /**
     * Convierte una entidad Socio en su correspondiente SocioDTO para la vista.
     */
    public SocioDTO toDTO(Socio socio, EstadoCuota estadoCuota) {
        if (socio == null) {
            return null;
        }

        SocioDTO dto = new SocioDTO();
        dto.setId(socio.getId());
        dto.setNumeroSocio(socio.getNumeroSocio());
        dto.setDni(socio.getDni());
        dto.setNombre(socio.getNombre());
        dto.setApellido(socio.getApellido());
        dto.setNombreCompleto(socio.getNombreCompleto());
        dto.setEmail(socio.getEmail());
        dto.setTelefono(socio.getTelefono());
        dto.setDireccion(socio.getDireccion());
        dto.setFechaNacimiento(socio.getFechaNacimiento());
        dto.setFechaAlta(socio.getFechaAlta());
        dto.setFotoRostro(socio.getFotoRostro());
        dto.setActivo(socio.isActivo());
        int countFamiliares = (socio.getFamiliares() != null ? socio.getFamiliares().size() : 0) +
                              (socio.getSociosFamiliares() != null ? socio.getSociosFamiliares().size() : 0);
        dto.setCantidadFamiliares(countFamiliares);
        dto.setEstadoCuota(estadoCuota != null ? estadoCuota : EstadoCuota.ADEUDA);
        dto.setTipoSocio(socio.getTipoSocio());
        if (socio.getSocioTitular() != null) {
            dto.setSocioTitularId(socio.getSocioTitular().getId());
            dto.setSocioTitularNombreCompleto(socio.getSocioTitular().getNombreCompleto());
        }
        dto.setParentesco(socio.getParentesco());

        return dto;
    }

    /**
     * Transforma un DTO de formulario en una nueva entidad de dominio Socio.
     */
    public Socio toEntity(SocioFormDTO formDTO, String nombreArchivoFoto) {
        if (formDTO == null) {
            return null;
        }

        Socio socio = new Socio();
        socio.setId(formDTO.getId());
        socio.setDni(formDTO.getDni());
        socio.setNombre(formDTO.getNombre());
        socio.setApellido(formDTO.getApellido());
        socio.setEmail(formDTO.getEmail());
        socio.setTelefono(formDTO.getTelefono());
        socio.setDireccion(formDTO.getDireccion());
        socio.setFechaNacimiento(formDTO.getFechaNacimiento());
        socio.setActivo(formDTO.isActivo());
        socio.setFotoRostro(nombreArchivoFoto);
        socio.setTipoSocio(formDTO.getTipoSocio());
        socio.setParentesco(formDTO.getParentesco());

        return socio;
    }

    /**
     * Convierte una entidad existente en su DTO de formulario para pre-cargar datos en la edición.
     */
    public SocioFormDTO toFormDTO(Socio socio) {
        if (socio == null) {
            return null;
        }

        SocioFormDTO form = new SocioFormDTO();
        form.setId(socio.getId());
        form.setDni(socio.getDni());
        form.setNombre(socio.getNombre());
        form.setApellido(socio.getApellido());
        form.setEmail(socio.getEmail());
        form.setTelefono(socio.getTelefono());
        form.setDireccion(socio.getDireccion());
        form.setFechaNacimiento(socio.getFechaNacimiento());
        form.setFotoRostroActual(socio.getFotoRostro());
        form.setActivo(socio.isActivo());
        form.setTipoSocio(socio.getTipoSocio());
        if (socio.getSocioTitular() != null) {
            form.setSocioTitularId(socio.getSocioTitular().getId());
        }
        form.setParentesco(socio.getParentesco());

        return form;
    }

    /**
     * Actualiza los campos modificables de una entidad existente a partir del formulario.
     */
    public void updateEntityFromForm(Socio socio, SocioFormDTO formDTO, String nuevoArchivoFoto) {
        socio.setNombre(formDTO.getNombre());
        socio.setApellido(formDTO.getApellido());
        socio.setEmail(formDTO.getEmail());
        socio.setTelefono(formDTO.getTelefono());
        socio.setDireccion(formDTO.getDireccion());
        socio.setFechaNacimiento(formDTO.getFechaNacimiento());
        socio.setActivo(formDTO.isActivo());
        socio.setParentesco(formDTO.getParentesco());

        if (nuevoArchivoFoto != null && !nuevoArchivoFoto.isBlank()) {
            socio.setFotoRostro(nuevoArchivoFoto);
        }
    }
}
