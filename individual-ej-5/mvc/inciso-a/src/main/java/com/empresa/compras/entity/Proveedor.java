package com.empresa.compras.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity: Proveedor
 * ----------------------------------------------------------------------
 * Proveedor mayorista de productos de tecnologia al cual la empresa le
 * realiza Ordenes de Compra.
 *
 * Relacion 1:N con OrdenCompra (un proveedor puede tener muchas ordenes).
 * mappedBy="proveedor" indica que la clave foranea vive del lado de
 * OrdenCompra (dueño de la relacion), evitando una tabla intermedia.
 */
@Entity
@Table(name = "proveedores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "cuit", nullable = false, unique = true, length = 20)
    private String cuit;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Builder.Default
    @OneToMany(mappedBy = "proveedor")
    private List<OrdenCompra> ordenesCompra = new ArrayList<>();
}
