package com.clubdeportivo.dto;

import com.clubdeportivo.model.enums.MedioPago;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ========================================================================================
 * DTO DE FORMULARIO: PagoCuotaFormDTO
 * ========================================================================================
 * Objeto de comando para registrar la cancelación de la cuota social familiar.
 * Admite Efectivo, Transferencia y Mercado Pago con validaciones de importes.
 * ========================================================================================
 */
public class PagoCuotaFormDTO {

    @NotNull(message = "Debe seleccionar el socio titular")
    private Long socioId;

    private String socioNombreCompleto;

    @NotNull(message = "El mes del período es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer periodoMes;

    @NotNull(message = "El año del período es obligatorio")
    @Min(value = 2020, message = "El año no es válido")
    @Max(value = 2035, message = "El año no es válido")
    private Integer periodoAnio;

    @NotNull(message = "El importe a cobrar es obligatorio")
    @DecimalMin(value = "1.00", message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotNull(message = "Debe seleccionar un medio de pago válido")
    private MedioPago medioPago;

    @Size(max = 255, message = "Las observaciones no pueden superar los 255 caracteres")
    private String observaciones;

    public PagoCuotaFormDTO() {
        LocalDate hoy = LocalDate.now();
        this.periodoMes = hoy.getMonthValue();
        this.periodoAnio = hoy.getYear();
        // Valor cuota base estándar para el grupo familiar
        this.monto = new BigDecimal("15000.00");
        this.medioPago = MedioPago.EFECTIVO;
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

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public void setMedioPago(MedioPago medioPago) {
        this.medioPago = medioPago;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
