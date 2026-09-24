package com.clubdeportivo.repository;

import com.clubdeportivo.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ========================================================================================
 * REPOSITORIO JPA: SocioRepository
 * ========================================================================================
 * Encapsula el acceso a datos para los socios titulares.
 *
 * Consultas derivadas por nombre de método:
 * Spring Data JPA analiza el nombre del método (ej: findByDni) y sintetiza en tiempo
 * de arranque la consulta JPQL/SQL correspondiente de forma automática.
 * ========================================================================================
 */
@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {

    /**
     * Recupera un socio por su Documento Nacional de Identidad.
     */
    Optional<Socio> findByDni(String dni);

    /**
     * Recupera un socio por su número de carnet/matrícula de socio.
     */
    Optional<Socio> findByNumeroSocio(String numeroSocio);

    /**
     * Valida si ya existe un socio registrado con dicho DNI.
     */
    boolean existsByDni(String dni);

    /**
     * Valida si ya existe un socio registrado con dicho número de socio.
     */
    boolean existsByNumeroSocio(String numeroSocio);

    /**
     * Lista únicamente los socios activos.
     */
    List<Socio> findByActivoTrueOrderByApellidoAscNombreAsc();

    /**
     * Búsqueda dinámica con filtro por nombre, apellido o DNI para la interfaz de búsqueda rápida.
     */
    @Query("SELECT s FROM Socio s WHERE s.activo = true AND (" +
            "LOWER(s.nombre) LIKE LOWER(CONCAT('%', :criterio, '%')) OR " +
            "LOWER(s.apellido) LIKE LOWER(CONCAT('%', :criterio, '%')) OR " +
            "s.dni LIKE CONCAT('%', :criterio, '%') OR " +
            "s.numeroSocio LIKE CONCAT('%', :criterio, '%')) " +
            "ORDER BY s.apellido ASC, s.nombre ASC")
    List<Socio> buscarPorCriterio(@Param("criterio") String criterio);

    /**
     * Conteo de socios activos para las tarjetas de métricas del Dashboard.
     */
    long countByActivoTrue();

    /**
     * Recupera los socios familiares dependientes de un socio titular específico.
     */
    List<Socio> findBySocioTitularIdAndActivoTrue(Long socioTitularId);

    /**
     * Recupera todos los socios titulares activos que pueden actuar como cabeza de familia.
     * Considera socios con tipoSocio = TITULAR y registros históricos donde tipoSocio es NULL y socioTitular es NULL.
     * Permite excluir opcionalmente un ID (para evitar que un socio se asocie como titular de sí mismo en edición).
     */
    @Query("SELECT s FROM Socio s WHERE s.activo = true AND " +
           "(s.tipoSocio = com.clubdeportivo.model.enums.TipoSocio.TITULAR OR (s.tipoSocio IS NULL AND s.socioTitular IS NULL)) " +
           "AND (:excluirId IS NULL OR s.id <> :excluirId) " +
           "ORDER BY s.apellido ASC, s.nombre ASC")
    List<Socio> findSociosTitularesActivos(@Param("excluirId") Long excluirId);
}
