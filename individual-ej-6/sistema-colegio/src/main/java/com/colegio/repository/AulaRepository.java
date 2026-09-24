package com.colegio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.colegio.model.entity.Aula;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

    List<Aula> findByGradoIdOrderByDivisionAsc(Long gradoId);

    boolean existsByGradoIdAndDivisionIgnoreCase(Long gradoId, String division);
}
