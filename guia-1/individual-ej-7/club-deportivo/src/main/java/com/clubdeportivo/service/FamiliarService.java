package com.clubdeportivo.service;

import com.clubdeportivo.dto.FamiliarDTO;
import com.clubdeportivo.dto.FamiliarFormDTO;

import java.util.List;

/**
 * ========================================================================================
 * INTERFAZ DE SERVICIO: FamiliarService
 * ========================================================================================
 * Contrato de operaciones para la administración de los grupos familiares asociados a socios.
 * ========================================================================================
 */
public interface FamiliarService {

    List<FamiliarDTO> listarPorSocio(Long socioId);

    FamiliarDTO obtenerPorId(Long id);

    FamiliarFormDTO obtenerFormularioPorId(Long id);

    FamiliarDTO registrarFamiliar(FamiliarFormDTO formDTO);

    FamiliarDTO actualizarFamiliar(FamiliarFormDTO formDTO);

    void darDeBaja(Long id);

    long contarFamiliaresActivos();
}
