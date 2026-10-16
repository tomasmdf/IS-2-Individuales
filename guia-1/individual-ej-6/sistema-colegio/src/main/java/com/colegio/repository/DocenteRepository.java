package com.colegio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.colegio.model.entity.Docente;

/**
 * =============================================================================
 * CAPA REPOSITORY (persistencia / ORM)
 * =============================================================================
 * Al extender JpaRepository<Docente, Long> heredamos "gratis" (sin escribir
 * SQL ni implementación) los métodos CRUD básicos: save, findById, findAll,
 * deleteById, count, existsById, etc. Spring Data JPA genera la
 * implementación en tiempo de ejecución mediante un proxy.
 *
 * Los métodos "findByCorreo", "existsByCorreo" son "query methods": Spring
 * Data JPA interpreta el NOMBRE del método y construye automáticamente la
 * consulta JPQL/SQL equivalente (Derived Query Methods), sin necesidad de
 * anotar con @Query en estos casos simples.
 * =============================================================================
 */
@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {

    /** Usado por Spring Security para autenticar: el "username" es el correo. */
    Optional<Docente> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Docente> findByActivoTrueOrderByApellidoAscNombreAsc();
}
