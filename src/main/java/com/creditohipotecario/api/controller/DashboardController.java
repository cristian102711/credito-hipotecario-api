package com.creditohipotecario.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.creditohipotecario.api.dto.DashboardResponse;
import com.creditohipotecario.api.model.EstadoSolicitud;
import com.creditohipotecario.api.repository.SolicitudCreditoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "Estadísticas y métricas del sistema")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final SolicitudCreditoRepository solicitudRepository;

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('EJECUTIVO') or hasRole('ADMIN')")
    public ResponseEntity<DashboardResponse> getEstadisticas() {
        DashboardResponse stats = DashboardResponse.builder()
                .totalSolicitudes(solicitudRepository.count())
                .solicitudesBorrador(solicitudRepository.countByEstado(EstadoSolicitud.BORRADOR))
                .solicitudesEnviadas(solicitudRepository.countByEstado(EstadoSolicitud.ENVIADA))
                .solicitudesEnRevision(solicitudRepository.countByEstado(EstadoSolicitud.EN_REVISION))
                .solicitudesAprobadas(solicitudRepository.countByEstado(EstadoSolicitud.APROBADA))
                .solicitudesRechazadas(solicitudRepository.countByEstado(EstadoSolicitud.RECHAZADA))
                .montoTotalAprobado(solicitudRepository.sumMontoByEstado(EstadoSolicitud.APROBADA))
                .montoTotalSolicitado(solicitudRepository.sumMontoTotal())
                .build();

        return ResponseEntity.ok(stats);
    }
}