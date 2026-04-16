package com.creditohipotecario.api.service;

import com.creditohipotecario.api.dto.SolicitudCreditoRequest;
import com.creditohipotecario.api.dto.SolicitudCreditoResponse;
import com.creditohipotecario.api.model.*;
import com.creditohipotecario.api.repository.PropiedadRepository;
import com.creditohipotecario.api.repository.SolicitudCreditoRepository;
import com.creditohipotecario.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudCreditoService {

    private static final BigDecimal TASA_ANUAL = new BigDecimal("0.0489");
    private final SolicitudCreditoRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final PropiedadRepository propiedadRepository;
    private final NotificacionService notificacionService;

    public SolicitudCreditoResponse crear(SolicitudCreditoRequest request) {
        Usuario usuario = getUsuarioAutenticado();
        Propiedad propiedad = propiedadRepository.findById(request.getPropiedadId())
                .orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));

        if (propiedad.getEstado() != EstadoPropiedad.DISPONIBLE) {
            throw new RuntimeException("La propiedad no está disponible");
        }

        boolean yaExiste = solicitudRepository.existsByUsuarioIdAndPropiedadIdAndEstadoNotIn(
                usuario.getId(), propiedad.getId(),
                List.of(EstadoSolicitud.RECHAZADA));
        if (yaExiste) {
            throw new RuntimeException("Ya tienes una solicitud activa para esta propiedad");
        }

        BigDecimal tasaMensual = TASA_ANUAL.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int cuotas = request.getPlazoAnos() * 12;
        BigDecimal dividendo = calcularDividendo(request.getMontoSolicitado(), tasaMensual, cuotas);
        BigDecimal cae = calcularCAE(tasaMensual);

        SolicitudCredito solicitud = SolicitudCredito.builder()
                .usuario(usuario)
                .propiedad(propiedad)
                .montoSolicitado(request.getMontoSolicitado())
                .plazoAnos(request.getPlazoAnos())
                .tasaInteres(TASA_ANUAL)
                .dividendoMensual(dividendo)
                .cae(cae)
                .observaciones(request.getObservaciones())
                .build();

        SolicitudCredito guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudRecibida(guardada);
        return SolicitudCreditoResponse.from(guardada);
    }

    public Page<SolicitudCreditoResponse> listarMisSolicitudes(Pageable pageable) {
        Usuario usuario = getUsuarioAutenticado();
        return solicitudRepository.findByUsuarioId(usuario.getId(), pageable)
                .map(SolicitudCreditoResponse::from);
    }

    public Page<SolicitudCreditoResponse> listarTodas(Pageable pageable) {
        return solicitudRepository.findAll(pageable)
                .map(SolicitudCreditoResponse::from);
    }

    public Page<SolicitudCreditoResponse> listarPorEstado(EstadoSolicitud estado, Pageable pageable) {
        return solicitudRepository.findByEstado(estado, pageable)
                .map(SolicitudCreditoResponse::from);
    }

    public SolicitudCreditoResponse obtener(Long id) {
        return SolicitudCreditoResponse.from(buscarSolicitud(id));
    }

    public SolicitudCreditoResponse cambiarEstado(Long id, EstadoSolicitud nuevoEstado, String observaciones) {
        SolicitudCredito solicitud = buscarSolicitud(id);
        validarTransicion(solicitud.getEstado(), nuevoEstado);
        solicitud.setEstado(nuevoEstado);
        if (observaciones != null) solicitud.setObservaciones(observaciones);
        SolicitudCredito guardada = solicitudRepository.save(solicitud);

        switch (nuevoEstado) {
            case APROBADA -> notificacionService.notificarSolicitudAprobada(guardada);
            case RECHAZADA -> notificacionService.notificarSolicitudRechazada(guardada);
            default -> {}
        }

        return SolicitudCreditoResponse.from(guardada);
    }

    public SolicitudCreditoResponse enviar(Long id) {
        SolicitudCredito solicitud = buscarSolicitud(id);
        if (solicitud.getEstado() != EstadoSolicitud.BORRADOR) {
            throw new RuntimeException("Solo se pueden enviar solicitudes en estado BORRADOR");
        }
        solicitud.setEstado(EstadoSolicitud.ENVIADA);
        SolicitudCredito guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudRecibida(guardada);
        return SolicitudCreditoResponse.from(guardada);
    }

    // Fórmula: M = P * [r(1+r)^n] / [(1+r)^n - 1]
    private BigDecimal calcularDividendo(BigDecimal monto, BigDecimal tasaMensual, int cuotas) {
        BigDecimal unoPlusTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal potencia = unoPlusTasa.pow(cuotas, new MathContext(15));
        BigDecimal numerador = tasaMensual.multiply(potencia);
        BigDecimal denominador = potencia.subtract(BigDecimal.ONE);
        return monto.multiply(numerador.divide(denominador, 10, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // CAE anual aproximado
    private BigDecimal calcularCAE(BigDecimal tasaMensual) {
        BigDecimal unoPlusTasa = BigDecimal.ONE.add(tasaMensual);
        BigDecimal caeAnual = unoPlusTasa.pow(12, new MathContext(10)).subtract(BigDecimal.ONE);
        return caeAnual.setScale(4, RoundingMode.HALF_UP);
    }

    private void validarTransicion(EstadoSolicitud actual, EstadoSolicitud nuevo) {
        boolean valida = switch (actual) {
            case BORRADOR -> nuevo == EstadoSolicitud.ENVIADA;
            case ENVIADA -> nuevo == EstadoSolicitud.EN_REVISION || nuevo == EstadoSolicitud.RECHAZADA;
            case EN_REVISION -> nuevo == EstadoSolicitud.APROBADA || nuevo == EstadoSolicitud.RECHAZADA;
            default -> false;
        };
        if (!valida) {
            throw new RuntimeException("Transición inválida: " + actual + " → " + nuevo);
        }
    }

    private SolicitudCredito buscarSolicitud(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + id));
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}