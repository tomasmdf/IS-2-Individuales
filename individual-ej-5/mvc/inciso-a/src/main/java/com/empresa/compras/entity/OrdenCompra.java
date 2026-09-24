package com.empresa.compras.entity;

import com.empresa.compras.enums.EstadoOrdenCompra;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * Entity: OrdenCompra (cabecera)
 * ============================================================================
 * Documento mediante el cual la empresa le compra mercaderia a un Proveedor
 * mayorista. Es la cabecera de una relacion maestro-detalle con
 * DetalleOrdenCompra (1 orden -> N detalles, uno por cada producto comprado).
 *
 * cascade = CascadeType.ALL + orphanRemoval = true sobre "detalles":
 *   - Al guardar/eliminar la orden, sus detalles se guardan/eliminan en
 *     cascada automaticamente (no hace falta un repository aparte para
 *     administrar el detalle de forma independiente).
 *
 * numero: numero correlativo de la orden (visible para el usuario), distinto
 *         del "id" tecnico autogenerado.
 * ============================================================================
 */
@Entity
@Table(name = "ordenes_compra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", nullable = false, unique = true, length = 20)
    private String numero;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    /** Usuario del sistema que registro la orden de compra (auditoria minima). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoOrdenCompra estado;

    @Column(name = "total", nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @Column(name = "observaciones", length = 300)
    private String observaciones;

    @Builder.Default
    @OneToMany(mappedBy = "ordenCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrdenCompra> detalles = new ArrayList<>();

    /** Metodo de conveniencia para mantener sincronizadas ambas puntas de la relacion. */
    public void agregarDetalle(DetalleOrdenCompra detalle) {
        detalle.setOrdenCompra(this);
        this.detalles.add(detalle);
    }
}
