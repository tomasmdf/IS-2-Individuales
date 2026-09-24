package com.colegio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.colegio.model.entity.Asignacion;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    List<Asignacion> findByDocenteIdOrderByMateriaNombreAsc(Long docenteId);

    List<Asignacion> findByDocenteCorreoOrderByMateriaNombreAsc(String correoDocente);

    boolean existsByDocenteIdAndMateriaIdAndAulaId(Long docenteId, Long materiaId, Long aulaId);
}
