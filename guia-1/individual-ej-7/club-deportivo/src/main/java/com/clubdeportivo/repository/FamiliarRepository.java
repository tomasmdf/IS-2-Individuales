package com.clubdeportivo.repository;

import com.clubdeportivo.model.Familiar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ========================================================================================
 * REPOSITORIO JPA: FamiliarRepository
 * ========================================================================================
 * Encapsula las operaciones sobre los integrantes de los grupos familiares.
 * ========================================================================================
 */
@Repository
public interface FamiliarRepository extends JpaRepository<Familiar, Long> {

    /**
     * Busca un familiar por su DNI (empleado en el control de acceso perimetral).
     */
    Optional<Familiar> findByDni(String dni);

    /**
     * Valida si un DNI ya se encuentra registrado como familiar.
     */
    boolean existsByDni(String dni);

    /**
     * Obtiene todos los familiares activos asociados a un socio titular específico.
     */
    List<Familiar> findBySocioIdAndActivoTrueOrderByApellidoAscNombreAsc(Long socioId);

    /**
     * Conteo total de familiares registrados activos en el club.
     */
    long countByActivoTrue();
}
