package com.creditohipotecario.api.dto;

import com.creditohipotecario.api.model.EstadoSolicitud;
import com.creditohipotecario.api.model.SolicitudCredito;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SolicitudCreditoResponse {

    private Long id;
    private Long usuarioId;
    private String nombreUsuario;
    private Long propiedadId;
    private String titulopropiedad;
    private BigDecimal montoSolicitado;
    private Integer plazoAnos;
    private BigDecimal tasaInteres;
    private BigDecimal dividendoMensual;
    private BigDecimal cae;
    private EstadoSolicitud estado;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SolicitudCreditoResponse from(SolicitudCredito s) {
        return SolicitudCreditoResponse.builder()
                .id(s.getId())
                .usuarioId(s.getUsuario().getId())
                .nombreUsuario(s.getUsuario().getNombre() + " " + s.getUsuario().getApellido())
                .propiedadId(s.getPropiedad().getId())
                .titulopropiedad(s.getPropiedad().getTitulo())
                .montoSolicitado(s.getMontoSolicitado())
                .plazoAnos(s.getPlazoAnos())
                .tasaInteres(s.getTasaInteres())
                .dividendoMensual(s.getDividendoMensual())
                .cae(s.getCae())
                .estado(s.getEstado())
                .observaciones(s.getObservaciones())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}