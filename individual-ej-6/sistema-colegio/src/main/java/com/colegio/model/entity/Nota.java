package com.colegio.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.colegio.model.enums.Periodo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
 * ENTIDAD: Nota. La calificación de un Alumno en una Asignacion (=materia
 * dictada por un docente en un aula) durante un Periodo (trimestre) dado.
 * UNIQUE(alumno_id, asignacion_id, periodo): un alumno tiene una sola nota
 * por materia y por trimestre (se puede editar, no duplicar).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"alumno", "asignacion"})
@EqualsAndHashCode(callSuper = false, of = "id")
@Entity
@Table(name = "notas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "asignacion_id", "periodo"}))
public class Nota extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "alumno_id", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignacion_id", nullable = false)
    private Asignacion asignacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Periodo periodo;

    /** Calificación de 1.00 a 10.00 (dos decimales). */
    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal valor;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "fecha_carga", nullable = false)
    private LocalDate fechaCarga;
}
