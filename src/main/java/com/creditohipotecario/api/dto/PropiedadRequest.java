package com.creditohipotecario.api.dto;

import com.creditohipotecario.api.model.TipoPropiedad;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropiedadRequest {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    @NotBlank(message = "La región es obligatoria")
    private String region;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "La superficie es obligatoria")
    @Positive(message = "La superficie debe ser mayor a 0")
    private Double superficieM2;

    @NotNull(message = "Los dormitorios son obligatorios")
    @Min(value = 0, message = "Los dormitorios no pueden ser negativos")
    private Integer dormitorios;

    @NotNull(message = "Los baños son obligatorios")
    @Min(value = 1, message = "Debe tener al menos 1 baño")
    private Integer banos;

    @NotNull(message = "El tipo es obligatorio")
    private TipoPropiedad tipo;
}