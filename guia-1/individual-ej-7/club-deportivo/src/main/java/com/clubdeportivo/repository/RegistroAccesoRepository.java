package com.clubdeportivo.repository;

import com.clubdeportivo.model.RegistroAcceso;
import com.clubdeportivo.model.enums.TipoAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ========================================================================================
 * REPOSITORIO JPA: RegistroAccesoRepository
 * ========================================================================================
 * Provee consultas optimizadas sobre los eventos de ingreso y egreso del club.
 * ========================================================================================
 */
@Repository
public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {

    /**
     * Recupera el movimiento más reciente de una persona para conocer si se encuentra
     * actualmente dentro o fuera del club.
     */
    Optional<RegistroAcceso> findTopByDniPersonaOrderByFechaHoraDesc(String dniPersona);

    /**
     * Conteo de accesos por tipo (Entrada o Salida) dentro de una franja temporal (ej: el día de hoy).
     */
    long countByTipoAccesoAndFechaHoraBetween(TipoAcceso tipoAcceso, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Devuelve los últimos N accesos registrados para alimentar el monitor perimetral y el Dashboard.
     */
    List<RegistroAcceso> findTop20ByOrderByFechaHoraDesc();

    /**
     * Consulta con filtrado por rango de fechas para el módulo de auditoría de accesos.
     */
    List<RegistroAcceso> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Total de movimientos registrados en el día.
     */
    long countByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}
