package com.colegio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.colegio.model.entity.Grado;

@Repository
public interface GradoRepository extends JpaRepository<Grado, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
