package com.empresa.compras.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** Linea de detalle dentro del formulario/DTO de OrdenCompraDTO. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleOrdenCompraDTO {

    private Long id;

    @NotNull(message = "Debe seleccionar un producto")
    private Long productoId;

    /** Solo informativo para la vista (nombre y codigo del producto elegido). */
    private String productoNombre;
    private String productoCodigo;

    @NotNull(message = "Debe indicar la cantidad")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "Debe indicar el precio unitario")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
    private BigDecimal precioUnitario;

    /** Calculado por el Service: cantidad * precioUnitario. No se edita manualmente. */
    private BigDecimal subtotal;
}
