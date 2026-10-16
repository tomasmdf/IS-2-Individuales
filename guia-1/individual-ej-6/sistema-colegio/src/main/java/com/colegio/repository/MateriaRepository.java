package com.colegio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.colegio.model.entity.Materia;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
