package com.empresa.compras.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity: Producto
 * ----------------------------------------------------------------------
 * Producto de tecnologia comercializado por la empresa. El campo "stock"
 * es el que se ve afectado (incrementado) cuando una Orden de Compra pasa
 * al estado RECIBIDA (ver OrdenCompraServiceImpl.recibirOrden()).
 *
 * @ManyToOne hacia Categoria: muchos productos pertenecen a una categoria.
 * FetchType.LAZY evita traer la categoria completa salvo que se acceda
 * explicitamente a ella (mejora de rendimiento tipica en ORM).
 */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(name = "precio_compra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    /** Cantidad de unidades disponibles en stock. Se actualiza por compras. */
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "activo", nullable = false)
    private boolean activo;
}
