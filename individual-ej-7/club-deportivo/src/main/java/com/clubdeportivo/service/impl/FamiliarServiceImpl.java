package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.FamiliarDTO;
import com.clubdeportivo.dto.FamiliarFormDTO;
import com.clubdeportivo.mapper.FamiliarMapper;
import com.clubdeportivo.model.Familiar;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.repository.FamiliarRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.service.FamiliarService;
import com.clubdeportivo.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ========================================================================================
 * IMPLEMENTACIÓN DEL SERVICIO: FamiliarServiceImpl
 * ========================================================================================
 * Gestiona el alta, modificación y eliminación de integrantes del grupo familiar
 * garantizando la unicidad de identificación y el almacenamiento de fotografías faciales.
 * ========================================================================================
 */
@Service
@Transactional(readOnly = true)
public class FamiliarServiceImpl implements FamiliarService {

    private final FamiliarRepository familiarRepository;
    private final SocioRepository socioRepository;
    private final FamiliarMapper familiarMapper;
    private final FileStorageService fileStorageService;

    public FamiliarServiceImpl(FamiliarRepository familiarRepository,
                               SocioRepository socioRepository,
                               FamiliarMapper familiarMapper,
                               FileStorageService fileStorageService) {
        this.familiarRepository = familiarRepository;
        this.socioRepository = socioRepository;
        this.familiarMapper = familiarMapper;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public List<FamiliarDTO> listarPorSocio(Long socioId) {
        return familiarRepository.findBySocioIdAndActivoTrueOrderByApellidoAscNombreAsc(socioId).stream()
                .map(familiarMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FamiliarDTO obtenerPorId(Long id) {
        Familiar familiar = familiarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el familiar con ID: " + id));
        return familiarMapper.toDTO(familiar);
    }

    @Override
    public FamiliarFormDTO obtenerFormularioPorId(Long id) {
        Familiar familiar = familiarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el familiar con ID: " + id));
        return familiarMapper.toFormDTO(familiar);
    }

    @Override
    @Transactional
    public FamiliarDTO registrarFamiliar(FamiliarFormDTO formDTO) {
        // Valida que el socio titular exista y esté activo
        Socio socio = socioRepository.findById(formDTO.getSocioId())
                .orElseThrow(() -> new IllegalArgumentException("El socio titular especificado no existe"));

        if (!socio.isActivo()) {
            throw new IllegalStateException("No se pueden agregar familiares a un socio dado de baja");
        }

        // Valida unicidad de DNI
        if (familiarRepository.existsByDni(formDTO.getDni()) || socioRepository.existsByDni(formDTO.getDni())) {
            throw new IllegalArgumentException("Ya existe una persona registrada en el club con el DNI " + formDTO.getDni());
        }

        // Almacena la fotografía facial para el control perimetral
        String nombreFoto = null;
        if (formDTO.getFotoArchivo() != null && !formDTO.getFotoArchivo().isEmpty()) {
            nombreFoto = fileStorageService.almacenarArchivo(formDTO.getFotoArchivo());
        }

        Familiar familiar = familiarMapper.toEntity(formDTO, socio, nombreFoto);
        Familiar familiarGuardado = familiarRepository.save(familiar);

        return familiarMapper.toDTO(familiarGuardado);
    }

    @Override
    @Transactional
    public FamiliarDTO actualizarFamiliar(FamiliarFormDTO formDTO) {
        Familiar familiar = familiarRepository.findById(formDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el familiar con ID: " + formDTO.getId()));

        String nuevaFoto = null;
        if (formDTO.getFotoArchivo() != null && !formDTO.getFotoArchivo().isEmpty()) {
            nuevaFoto = fileStorageService.almacenarArchivo(formDTO.getFotoArchivo());
            if (familiar.getFotoRostro() != null) {
                fileStorageService.eliminarArchivo(familiar.getFotoRostro());
            }
        }

        familiarMapper.updateEntityFromForm(familiar, formDTO, nuevaFoto);
        Familiar familiarActualizado = familiarRepository.save(familiar);

        return familiarMapper.toDTO(familiarActualizado);
    }

    @Override
    @Transactional
    public void darDeBaja(Long id) {
        Familiar familiar = familiarRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el familiar con ID: " + id));
        familiar.setActivo(false);
        familiarRepository.save(familiar);
    }

    @Override
    public long contarFamiliaresActivos() {
        return familiarRepository.countByActivoTrue();
    }
}
