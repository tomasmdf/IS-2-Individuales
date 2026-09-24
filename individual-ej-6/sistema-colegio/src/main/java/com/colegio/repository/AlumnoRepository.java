package com.colegio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.colegio.model.entity.Alumno;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    List<Alumno> findByAulaIdOrderByApellidoAscNombreAsc(Long aulaId);

    List<Alumno> findByGradoIdOrderByApellidoAscNombreAsc(Long gradoId);

    boolean existsByDni(String dni);

    /**
     * Alumnos de una asignación puntual (= de la aula asociada a esa
     * asignación). Se usa para armar la planilla de carga de notas del
     * docente: "alumnos del aula donde dicto esta materia".
     *
     * @Query con JPQL: se usa aquí (en vez de un derived query method)
     * porque la condición navega dos relaciones (a.aula.asignaciones) y
     * el nombre del método resultante sería demasiado largo y confuso.
     */
    @org.springframework.data.jpa.repository.Query(
        "select a from Alumno a where a.aula.id = " +
        "(select asg.aula.id from Asignacion asg where asg.id = :asignacionId) " +
        "order by a.apellido, a.nombre")
    List<Alumno> findAlumnosDeAsignacion(Long asignacionId);
}
