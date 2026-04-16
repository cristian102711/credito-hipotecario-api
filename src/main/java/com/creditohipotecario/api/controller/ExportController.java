package com.creditohipotecario.api.controller;

import com.creditohipotecario.api.model.EstadoSolicitud;
import com.creditohipotecario.api.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/pdf")
    @PreAuthorize("hasRole('EJECUTIVO') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) EstadoSolicitud estado) throws IOException {

        byte[] pdf = exportService.exportarSolicitudesPdf(estado);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=solicitudes.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/excel")
    @PreAuthorize("hasRole('EJECUTIVO') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) EstadoSolicitud estado) throws IOException {

        byte[] excel = exportService.exportarSolicitudesExcel(estado);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=solicitudes.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}