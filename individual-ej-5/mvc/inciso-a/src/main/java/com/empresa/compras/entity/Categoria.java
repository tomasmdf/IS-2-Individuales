package com.empresa.compras.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity: Categoria
 * ----------------------------------------------------------------------
 * Rubro/familia de los productos de tecnologia (ej: "Notebooks",
 * "Periféricos", "Almacenamiento", "Componentes"). Permite clasificar
 * el catalogo de Productos.
 */
@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 80)
    private String nombre;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    /** Lado inverso de la relacion Producto.categoria; usado para validar bajas. */
    @Builder.Default
    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos = new ArrayList<>();
}
