package com.empresa.compras.dto;

import com.empresa.compras.enums.EstadoOrdenCompra;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO maestro-detalle de la Orden de Compra. Agrupa la cabecera (proveedor,
 * fecha, estado) y la lista de DetalleOrdenCompraDTO, tal como se completa
 * en un unico formulario HTML (Thymeleaf) con filas dinamicas de detalle.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraDTO {

    private Long id;

    /** Se autogenera en el Service si viaja vacio (alta). */
    private String numero;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "Debe seleccionar un proveedor")
    private Long proveedorId;

    private String proveedorNombre;

    private Long usuarioId;
    private String usuarioNombre;

    private EstadoOrdenCompra estado;

    private BigDecimal total;

    @Size(max = 300)
    private String observaciones;

    @NotEmpty(message = "La orden debe tener al menos un producto en el detalle")
    @Valid
    @Builder.Default
    private List<DetalleOrdenCompraDTO> detalles = new ArrayList<>();
}
