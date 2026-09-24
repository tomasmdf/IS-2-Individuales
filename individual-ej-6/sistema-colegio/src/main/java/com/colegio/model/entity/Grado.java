package com.colegio.model.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * ENTIDAD: Grado (p.ej. "1er Año", "6to Grado"). Un Grado agrupa Aulas
 * (divisiones/secciones, p.ej. "1ero A", "1ero B") y, a través de éstas,
 * a los alumnos.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"aulas"})
@EqualsAndHashCode(callSuper = false, of = "id")
@Entity
@Table(name = "grados")
public class Grado extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ej: "1er Año", "2do Año" */
    @Column(nullable = false, unique = true, length = 60)
    private String nombre;

    /** Nivel educativo, ej: "Primario", "Secundario" */
    @Column(nullable = false, length = 40)
    private String nivel;

    @OneToMany(mappedBy = "grado")
    @Builder.Default
    private List<Aula> aulas = new ArrayList<>();
}
