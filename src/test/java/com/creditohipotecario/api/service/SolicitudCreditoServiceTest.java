package com.creditohipotecario.api.service;

import com.creditohipotecario.api.dto.SolicitudCreditoRequest;
import com.creditohipotecario.api.model.*;
import com.creditohipotecario.api.repository.PropiedadRepository;
import com.creditohipotecario.api.repository.SolicitudCreditoRepository;
import com.creditohipotecario.api.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // ← fix Error 2
class SolicitudCreditoServiceTest {

    @Mock private SolicitudCreditoRepository solicitudRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PropiedadRepository propiedadRepository;
    @Mock private NotificacionService notificacionService;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private SolicitudCreditoService solicitudService;

    private Usuario usuario;
    private Propiedad propiedad;
    private SolicitudCreditoRequest request;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L).nombre("Cristian").apellido("Velasquez")
                .email("cristian@test.com").rol(Rol.CLIENTE).build();

        propiedad = Propiedad.builder()
                .id(1L).titulo("Depto Providencia")
                .precio(new BigDecimal("85000000"))
                .estado(EstadoPropiedad.DISPONIBLE).build();

        request = new SolicitudCreditoRequest();
        request.setPropiedadId(1L);
        request.setMontoSolicitado(new BigDecimal("68000000"));
        request.setPlazoAnos(20);

        when(authentication.getName()).thenReturn("cristian@test.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void crear_cuandoDatosValidos_deberiaCalcularDividendoYCae() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(usuario));
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(solicitudRepository.existsByUsuarioIdAndPropiedadIdAndEstadoNotIn(
                any(), any(), any())).thenReturn(false);
        // fix Error 1: setear estado BORRADOR en el objeto guardado
        when(solicitudRepository.save(any())).thenAnswer(i -> {
            SolicitudCredito s = i.getArgument(0);
            if (s.getEstado() == null) s.setEstado(EstadoSolicitud.BORRADOR);
            return s;
        });

        var response = solicitudService.crear(request);

        assertNotNull(response.getDividendoMensual());
        assertNotNull(response.getCae());
        assertTrue(response.getDividendoMensual().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(EstadoSolicitud.BORRADOR, response.getEstado());
    }

    @Test
    void crear_cuandoPropiedadNoDisponible_deberiaLanzarExcepcion() {
        propiedad.setEstado(EstadoPropiedad.VENDIDA);
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(usuario));
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> solicitudService.crear(request));

        assertEquals("La propiedad no está disponible", ex.getMessage());
        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void crear_cuandoYaExisteSolicitudActiva_deberiaLanzarExcepcion() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.of(usuario));
        when(propiedadRepository.findById(1L)).thenReturn(Optional.of(propiedad));
        when(solicitudRepository.existsByUsuarioIdAndPropiedadIdAndEstadoNotIn(
                any(), any(), any())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> solicitudService.crear(request));

        assertEquals("Ya tienes una solicitud activa para esta propiedad", ex.getMessage());
    }

    @Test
    void enviar_cuandoEstadoBorrador_deberiacambiarAEnviada() {
        SolicitudCredito solicitud = SolicitudCredito.builder()
                .id(1L).estado(EstadoSolicitud.BORRADOR)
                .usuario(usuario).propiedad(propiedad)
                .montoSolicitado(new BigDecimal("68000000"))
                .plazoAnos(20).tasaInteres(new BigDecimal("0.0489"))
                .dividendoMensual(new BigDecimal("441234.56"))
                .cae(new BigDecimal("0.0500"))
                .build();

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var response = solicitudService.enviar(1L);

        assertEquals(EstadoSolicitud.ENVIADA, response.getEstado());
    }
}