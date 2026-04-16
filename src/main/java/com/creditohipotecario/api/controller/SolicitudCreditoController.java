package com.creditohipotecario.api.controller;

import com.creditohipotecario.api.dto.SolicitudCreditoRequest;
import com.creditohipotecario.api.dto.SolicitudCreditoResponse;
import com.creditohipotecario.api.model.EstadoSolicitud;
import com.creditohipotecario.api.service.SolicitudCreditoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudCreditoController {

    private final SolicitudCreditoService solicitudService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO', 'CLIENTE')")
    public ResponseEntity<SolicitudCreditoResponse> crear(
            @Valid @RequestBody SolicitudCreditoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.crear(request));
    }

    @GetMapping("/mis-solicitudes")
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO', 'CLIENTE')")
    public ResponseEntity<Page<SolicitudCreditoResponse>> misSolicitudes(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(solicitudService.listarMisSolicitudes(pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO')")
    public ResponseEntity<Page<SolicitudCreditoResponse>> listarTodas(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(solicitudService.listarTodas(pageable));
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO')")
    public ResponseEntity<Page<SolicitudCreditoResponse>> listarPorEstado(
            @PathVariable EstadoSolicitud estado,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(solicitudService.listarPorEstado(estado, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO', 'CLIENTE')")
    public ResponseEntity<SolicitudCreditoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.obtener(id));
    }

    @PostMapping("/{id}/enviar")
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO', 'CLIENTE')")
    public ResponseEntity<SolicitudCreditoResponse> enviar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.enviar(id));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO')")
    public ResponseEntity<SolicitudCreditoResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoSolicitud estado,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(solicitudService.cambiarEstado(id, estado, observaciones));
    }
}