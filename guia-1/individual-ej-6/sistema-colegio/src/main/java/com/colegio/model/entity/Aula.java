package com.colegio.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ENTIDAD: Aula (división/sección dentro de un Grado, p.ej. "A", "B").
 * Un Aula pertenece a un único Grado (@ManyToOne) y contiene Alumnos y
 * Asignaciones (qué docente dicta qué materia en esta aula).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"grado", "alumnos", "asignaciones"})
@EqualsAndHashCode(callSuper = false, of = "id")
@Entity
@Table(name = "aulas", uniqueConstraints = @UniqueConstraint(columnNames = {"grado_id", "division"}))
public class Aula extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** División/sección, ej: "A", "B", "Única" */
    @Column(nullable = false, length = 20)
    private String division;

    /** Capacidad máxima de alumnos (informativo). */
    @Column
    private Integer capacidad;

    /**
     * @ManyToOne(fetch = LAZY): muchas aulas pueden pertenecer a un mismo
     * grado; se declara LAZY explícitamente (a diferencia del default EAGER
     * de @ManyToOne) para no traer el Grado completo cada vez que se
     * consulta un Aula si no hace falta.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @OneToMany(mappedBy = "aula")
    @Builder.Default
    private List<Alumno> alumnos = new ArrayList<>();

    @OneToMany(mappedBy = "aula")
    @Builder.Default
    private List<Asignacion> asignaciones = new ArrayList<>();
}
