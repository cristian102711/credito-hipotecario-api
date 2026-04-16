package com.creditohipotecario.api.dto;

import com.creditohipotecario.api.model.EstadoPropiedad;
import com.creditohipotecario.api.model.Propiedad;
import com.creditohipotecario.api.model.TipoPropiedad;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PropiedadResponse {

    private Long id;
    private String titulo;
    private String descripcion;
    private String direccion;
    private String comuna;
    private String region;
    private BigDecimal precio;
    private Double superficieM2;
    private Integer dormitorios;
    private Integer banos;
    private TipoPropiedad tipo;
    private EstadoPropiedad estado;
    private LocalDateTime createdAt;

    public static PropiedadResponse from(Propiedad p) {
        return PropiedadResponse.builder()
                .id(p.getId())
                .titulo(p.getTitulo())
                .descripcion(p.getDescripcion())
                .direccion(p.getDireccion())
                .comuna(p.getComuna())
                .region(p.getRegion())
                .precio(p.getPrecio())
                .superficieM2(p.getSuperficieM2())
                .dormitorios(p.getDormitorios())
                .banos(p.getBanos())
                .tipo(p.getTipo())
                .estado(p.getEstado())
                .createdAt(p.getCreatedAt())
                .build();
    }
}