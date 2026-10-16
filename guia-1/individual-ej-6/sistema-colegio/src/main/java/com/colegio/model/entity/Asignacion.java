package com.colegio.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * ENTIDAD: Asignacion (tabla intermedia / "carga horaria").
 * Representa el hecho: "el Docente X dicta la Materia Y en el Aula Z".
 * Es la entidad clave del modelo: de aquí "cuelgan" las Notas (una Nota
 * siempre pertenece a una Asignación + un Alumno), y es lo que le permite al
 * sistema saber qué docente puede cargar notas de qué materia/aula.
 *
 * La restricción UNIQUE (docente_id, materia_id, aula_id) evita asignaciones
 * duplicadas (que el mismo docente quede cargado dos veces para la misma
 * materia y aula).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"docente", "materia", "aula"})
@EqualsAndHashCode(callSuper = false, of = "id")
@Entity
@Table(name = "asignaciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"docente_id", "materia_id", "aula_id"}))
public class Asignacion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "docente_id", nullable = false)
    private Docente docente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;
}
