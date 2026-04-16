package com.creditohipotecario.api.controller;

import com.creditohipotecario.api.dto.PropiedadRequest;
import com.creditohipotecario.api.dto.PropiedadResponse;
import com.creditohipotecario.api.model.EstadoPropiedad;
import com.creditohipotecario.api.model.TipoPropiedad;
import com.creditohipotecario.api.service.PropiedadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/propiedades")
@RequiredArgsConstructor
public class PropiedadController {

    private final PropiedadService propiedadService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO')")
    public ResponseEntity<PropiedadResponse> crear(@Valid @RequestBody PropiedadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(propiedadService.crear(request));
    }

    @GetMapping
    public ResponseEntity<Page<PropiedadResponse>> listar(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(propiedadService.listar(pageable));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<Page<PropiedadResponse>> filtrar(
            @RequestParam(required = false) EstadoPropiedad estado,
            @RequestParam(required = false) TipoPropiedad tipo,
            @RequestParam(required = false) String comuna,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(required = false) Integer dormitorios,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                propiedadService.filtrar(estado, tipo, comuna, precioMin, precioMax, dormitorios, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropiedadResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(propiedadService.obtener(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO')")
    public ResponseEntity<PropiedadResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PropiedadRequest request) {
        return ResponseEntity.ok(propiedadService.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'EJECUTIVO')")
    public ResponseEntity<PropiedadResponse> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPropiedad estado) {
        return ResponseEntity.ok(propiedadService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        propiedadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}