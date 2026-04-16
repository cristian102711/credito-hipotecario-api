package com.creditohipotecario.api.service;

import com.creditohipotecario.api.model.SolicitudCredito;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void notificarSolicitudRecibida(SolicitudCredito solicitud) {
        try {
            Context context = new Context();
            context.setVariable("nombreUsuario",
                solicitud.getUsuario().getNombre() + " " + solicitud.getUsuario().getApellido());
            context.setVariable("solicitudId", "#" + solicitud.getId());
            context.setVariable("tituloPropiedad", solicitud.getPropiedad().getTitulo());
            context.setVariable("montoSolicitado", formatMonto(solicitud.getMontoSolicitado()));
            context.setVariable("plazoAnos", solicitud.getPlazoAnos());
            context.setVariable("dividendoMensual", formatMonto(solicitud.getDividendoMensual()));

            String html = templateEngine.process("email/solicitud-recibida", context);
            enviarEmail(solicitud.getUsuario().getEmail(), "Solicitud de Crédito Recibida", html);
        } catch (Exception e) {
            log.error("Error enviando email solicitud recibida: {}", e.getMessage());
        }
    }

    @Async
    public void notificarSolicitudAprobada(SolicitudCredito solicitud) {
        try {
            Context context = new Context();
            context.setVariable("nombreUsuario",
                solicitud.getUsuario().getNombre() + " " + solicitud.getUsuario().getApellido());
            context.setVariable("solicitudId", "#" + solicitud.getId());
            context.setVariable("tituloPropiedad", solicitud.getPropiedad().getTitulo());
            context.setVariable("montoSolicitado", formatMonto(solicitud.getMontoSolicitado()));
            context.setVariable("dividendoMensual", formatMonto(solicitud.getDividendoMensual()));
            context.setVariable("tasaInteres", solicitud.getTasaInteres());

            String html = templateEngine.process("email/solicitud-aprobada", context);
            enviarEmail(solicitud.getUsuario().getEmail(), "¡Tu Crédito Hipotecario fue Aprobado!", html);
        } catch (Exception e) {
            log.error("Error enviando email solicitud aprobada: {}", e.getMessage());
        }
    }

    @Async
    public void notificarSolicitudRechazada(SolicitudCredito solicitud) {
        try {
            Context context = new Context();
            context.setVariable("nombreUsuario",
                solicitud.getUsuario().getNombre() + " " + solicitud.getUsuario().getApellido());
            context.setVariable("solicitudId", "#" + solicitud.getId());
            context.setVariable("tituloPropiedad", solicitud.getPropiedad().getTitulo());
            context.setVariable("observaciones",
                solicitud.getObservaciones() != null ? solicitud.getObservaciones() : "Sin observaciones");

            String html = templateEngine.process("email/solicitud-rechazada", context);
            enviarEmail(solicitud.getUsuario().getEmail(), "Resultado de tu Solicitud de Crédito", html);
        } catch (Exception e) {
            log.error("Error enviando email solicitud rechazada: {}", e.getMessage());
        }
    }

    private void enviarEmail(String destinatario, String asunto, String html) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(html, true);
        mailSender.send(message);
        log.info("Email enviado a {}: {}", destinatario, asunto);
    }

    private String formatMonto(BigDecimal monto) {
        if (monto == null) return "$0";
        NumberFormat fmt = NumberFormat.getCurrencyInstance(Locale.of("es", "CL"));
        return fmt.format(monto);
    }
}