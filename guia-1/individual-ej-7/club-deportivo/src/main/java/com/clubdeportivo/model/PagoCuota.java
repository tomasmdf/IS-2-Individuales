package com.clubdeportivo.model;

import com.clubdeportivo.model.enums.MedioPago;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ========================================================================================
 * ENTIDAD JPA: PagoCuota (Cobranza de Cuota Social Familiar)
 * ========================================================================================
 * Registra los pagos de cuotas societarias correspondientes a un grupo familiar (titular).
 *
 * Requerimiento de consigna:
 * "Se debe agregar la funcionalidad para poder registrar el pago de la cuota del club para cada familia,
 * donde el pago se puede realizar con distintos medios de pago (Efectivo, Transferencia, Mercado Pago)."
 *
 * Anotaciones utilizadas:
 * - @Entity & @Table: Mapeo de la tabla "pagos_cuota" con claves foráneas e índices de búsqueda por período.
 * - @ManyToOne(fetch = FetchType.LAZY): Vinculación con el socio titular que abona la cuota familiar.
 * - @Enumerated(EnumType.STRING): Persiste el canal de pago (EFECTIVO, TRANSFERENCIA, MERCADO_PAGO).
 * - BigDecimal: Tipo de dato estándar para importes financieros, garantizando precisión sin errores
 *   de redondeo propios de los tipos punto flotante (float/double).
 * ========================================================================================
 */
@Entity
@Table(name = "pagos_cuota", indexes = {
        @Index(name = "idx_pago_socio", columnList = "socio_id"),
        @Index(name = "idx_pago_periodo", columnList = "periodo_anio, periodo_mes"),
        @Index(name = "idx_pago_comprobante", columnList = "numero_comprobante", unique = true)
})
public class PagoCuota extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Socio titular que cancela la cuota del grupo familiar.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id", nullable = false, foreignKey = @ForeignKey(name = "fk_pago_socio"))
    private Socio socio;

    /**
     * Mes del período abonado (1 a 12).
     */
    @Column(name = "periodo_mes", nullable = false)
    private Integer periodoMes;

    /**
     * Año del período abonado (ej: 2026).
     */
    @Column(name = "periodo_anio", nullable = false)
    private Integer periodoAnio;

    /**
     * Importe abonado con precisión decimal de dos dígitos.
     */
    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    /**
     * Fecha y hora en la que se efectuó y registró la transacción de pago.
     */
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    /**
     * Medio de pago utilizado: Efectivo, Transferencia Bancaria o Mercado Pago.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "medio_pago", nullable = false, length = 30)
    private MedioPago medioPago;

    /**
     * Identificador o número de comprobante único generado para la factura/recibo.
     */
    @Column(name = "numero_comprobante", nullable = false, length = 50, unique = true)
    private String numeroComprobante;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    // ====================================================================================
    // CONSTRUCTORES
    // ====================================================================================

    public PagoCuota() {
        this.fechaPago = LocalDateTime.now();
    }

    public PagoCuota(Socio socio, Integer periodoMes, Integer periodoAnio, BigDecimal monto,
                     MedioPago medioPago, String numeroComprobante, String observaciones) {
        this.socio = socio;
        this.periodoMes = periodoMes;
        this.periodoAnio = periodoAnio;
        this.monto = monto;
        this.fechaPago = LocalDateTime.now();
        this.medioPago = medioPago;
        this.numeroComprobante = numeroComprobante;
        this.observaciones = observaciones;
    }

    // ====================================================================================
    // MÉTODOS DE CONVENIENCIA
    // ====================================================================================

    public String getPeriodoFormateado() {
        return String.format("%02d/%d", periodoMes, periodoAnio);
    }

    // ====================================================================================
    // GETTERS Y SETTERS
    // ====================================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }

    public Integer getPeriodoMes() {
        return periodoMes;
    }

    public void setPeriodoMes(Integer periodoMes) {
        this.periodoMes = periodoMes;
    }

    public Integer getPeriodoAnio() {
        return periodoAnio;
    }

    public void setPeriodoAnio(Integer periodoAnio) {
        this.periodoAnio = periodoAnio;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
