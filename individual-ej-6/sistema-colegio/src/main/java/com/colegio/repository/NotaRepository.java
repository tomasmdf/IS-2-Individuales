package com.colegio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.colegio.model.entity.Nota;
import com.colegio.model.enums.Periodo;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {

    List<Nota> findByAsignacionIdOrderByAlumnoApellidoAsc(Long asignacionId);

    List<Nota> findByAlumnoIdOrderByPeriodoAsc(Long alumnoId);

    Optional<Nota> findByAlumnoIdAndAsignacionIdAndPeriodo(Long alumnoId, Long asignacionId, Periodo periodo);
}
