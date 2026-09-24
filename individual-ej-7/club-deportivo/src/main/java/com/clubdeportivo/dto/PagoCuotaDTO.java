package com.clubdeportivo.dto;

import com.clubdeportivo.model.enums.MedioPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ========================================================================================
 * DTO DE RESPUESTA: PagoCuotaDTO
 * ========================================================================================
 * Encapsula la información de los pagos de cuotas para listados, comprobantes y recibos.
 * ========================================================================================
 */
public class PagoCuotaDTO {

    private Long id;
    private Long socioId;
    private String socioNombreCompleto;
    private String socioDni;
    private String socioNumero;
    private Integer periodoMes;
    private Integer periodoAnio;
    private String periodoFormateado;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private MedioPago medioPago;
    private String medioPagoEtiqueta;
    private String medioPagoIcono;
    private String medioPagoColorBadge;
    private String numeroComprobante;
    private String observaciones;
    private String creadoPor;

    public PagoCuotaDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSocioId() {
        return socioId;
    }

    public void setSocioId(Long socioId) {
        this.socioId = socioId;
    }

    public String getSocioNombreCompleto() {
        return socioNombreCompleto;
    }

    public void setSocioNombreCompleto(String socioNombreCompleto) {
        this.socioNombreCompleto = socioNombreCompleto;
    }

    public String getSocioDni() {
        return socioDni;
    }

    public void setSocioDni(String socioDni) {
        this.socioDni = socioDni;
    }

    public String getSocioNumero() {
        return socioNumero;
    }

    public void setSocioNumero(String socioNumero) {
        this.socioNumero = socioNumero;
    }

    public Integer getPeriodoMes() {
        return periodoMes;
    }

    public void setPeriodoMes(Integer periodoMes) {
        this.periodoMes = periodoMes;
        actualizarPeriodoFormateado();
    }

    public Integer getPeriodoAnio() {
        return periodoAnio;
    }

    public void setPeriodoAnio(Integer periodoAnio) {
        this.periodoAnio = periodoAnio;
        actualizarPeriodoFormateado();
    }

    private void actualizarPeriodoFormateado() {
        if (periodoMes != null && periodoAnio != null) {
            this.periodoFormateado = String.format("%02d/%d", periodoMes, periodoAnio);
        }
    }

    public String getPeriodoFormateado() {
        return periodoFormateado;
    }

    public void setPeriodoFormateado(String periodoFormateado) {
        this.periodoFormateado = periodoFormateado;
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
        if (medioPago != null) {
            this.medioPagoEtiqueta = medioPago.getEtiqueta();
            this.medioPagoIcono = medioPago.getIcono();
            this.medioPagoColorBadge = medioPago.getColorBadge();
        }
    }

    public String getMedioPagoEtiqueta() {
        return medioPagoEtiqueta;
    }

    public void setMedioPagoEtiqueta(String medioPagoEtiqueta) {
        this.medioPagoEtiqueta = medioPagoEtiqueta;
    }

    public String getMedioPagoIcono() {
        return medioPagoIcono;
    }

    public void setMedioPagoIcono(String medioPagoIcono) {
        this.medioPagoIcono = medioPagoIcono;
    }

    public String getMedioPagoColorBadge() {
        return medioPagoColorBadge;
    }

    public void setMedioPagoColorBadge(String medioPagoColorBadge) {
        this.medioPagoColorBadge = medioPagoColorBadge;
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

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }
}
