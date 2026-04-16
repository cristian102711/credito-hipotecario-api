package com.creditohipotecario.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {
    private long totalSolicitudes;
    private long solicitudesBorrador;
    private long solicitudesEnviadas;
    private long solicitudesEnRevision;
    private long solicitudesAprobadas;
    private long solicitudesRechazadas;
    private BigDecimal montoTotalAprobado;
    private BigDecimal montoTotalSolicitado;
}