package com.clubdeportivo.service;

import com.clubdeportivo.dto.SocioDTO;
import com.clubdeportivo.dto.SocioFormDTO;
import com.clubdeportivo.model.enums.EstadoCuota;

import java.util.List;

/**
 * ========================================================================================
 * INTERFAZ DE SERVICIO: SocioService
 * ========================================================================================
 * Define el contrato de operaciones de negocio para la gestión integral de socios titulares.
 *
 * Principio de inversión de dependencias (SOLID):
 * Los controladores dependen de esta abstracción y no de la implementación concreta,
 * facilitando la prueba con mocks y el desacoplamiento de la lógica de negocio.
 * ========================================================================================
 */
public interface SocioService {

    List<SocioDTO> listarTodosActivos();

    List<SocioDTO> buscarSocios(String criterio);

    SocioDTO obtenerPorId(Long id);

    SocioFormDTO obtenerFormularioPorId(Long id);

    SocioDTO registrarSocio(SocioFormDTO formDTO);

    SocioDTO actualizarSocio(SocioFormDTO formDTO);

    void darDeBaja(Long id);

    EstadoCuota calcularEstadoCuota(Long socioId);

    long contarSociosActivos();

    List<SocioDTO> listarSociosTitularesActivos();

    List<SocioDTO> listarSociosTitularesActivos(Long excluirId);

    List<SocioDTO> listarSociosFamiliaresPorTitular(Long titularId);
}
