package com.creditohipotecario.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SolicitudCreditoRequest {

    @NotNull(message = "El id de la propiedad es obligatorio")
    private Long propiedadId;

    @NotNull(message = "El monto solicitado es obligatorio")
    @DecimalMin(value = "1000000", message = "El monto mínimo es $1.000.000")
    private BigDecimal montoSolicitado;

    @NotNull(message = "El plazo es obligatorio")
    @Min(value = 5, message = "El plazo mínimo es 5 años")
    @Max(value = 30, message = "El plazo máximo es 30 años")
    private Integer plazoAnos;

    @Size(max = 500)
    private String observaciones;
}