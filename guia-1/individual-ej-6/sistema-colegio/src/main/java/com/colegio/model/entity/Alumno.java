package com.colegio.model.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.colegio.model.enums.Sexo;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ENTIDAD: Alumno. Pertenece a un Grado y a un Aula (división) concretos.
 * Se guarda "grado" además de "aula" para simplificar las consultas típicas
 * ("alumnos del 3er Año") sin tener que navegar aula -> grado en cada listado.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"grado", "aula", "notas"})
@EqualsAndHashCode(callSuper = false, of = "id")
@Entity
@Table(name = "alumnos")
public class Alumno extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    /** Documento Nacional de Identidad, único por alumno. */
    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Sexo sexo;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    @OneToMany(mappedBy = "alumno")
    @Builder.Default
    private List<Nota> notas = new ArrayList<>();
}
