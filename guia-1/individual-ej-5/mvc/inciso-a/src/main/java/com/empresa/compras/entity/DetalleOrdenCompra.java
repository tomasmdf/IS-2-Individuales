package com.empresa.compras.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity: DetalleOrdenCompra
 * ----------------------------------------------------------------------
 * Linea de detalle de una Orden de Compra: indica QUE producto se compro,
 * en que CANTIDAD y a que PRECIO UNITARIO (precio pactado con el proveedor
 * para esa compra, que puede diferir del precioCompra "de lista" del
 * producto). El "subtotal" = cantidad * precioUnitario se calcula en la
 * capa de Service al registrar la orden.
 */
@Entity
@Table(name = "detalle_ordenes_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleOrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_compra_id", nullable = false)
    private OrdenCompra ordenCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", nullable = false, precision = 14, scale = 2)
    private BigDecimal subtotal;
}
