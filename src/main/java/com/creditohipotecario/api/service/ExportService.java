package com.creditohipotecario.api.service;

import com.creditohipotecario.api.model.EstadoSolicitud;
import com.creditohipotecario.api.model.SolicitudCredito;
import com.creditohipotecario.api.repository.SolicitudCreditoRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

    private final SolicitudCreditoRepository solicitudRepository;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ─── PDF ────────────────────────────────────────────────────────────────

    public byte[] exportarSolicitudesPdf(EstadoSolicitud estado) throws IOException {
        List<SolicitudCredito> solicitudes = estado != null
                ? solicitudRepository.findByEstado(estado, org.springframework.data.domain.Pageable.unpaged()).getContent()
                : solicitudRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        document.add(new Paragraph("Reporte de Solicitudes de Crédito Hipotecario")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Subtítulo con filtro
        String filtro = estado != null ? "Estado: " + estado : "Todas las solicitudes";
        document.add(new Paragraph(filtro)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Tabla
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 3, 2, 2, 1.5f}))
                .useAllAvailableWidth();

        // Encabezados
        String[] headers = {"ID", "Cliente", "Propiedad", "Monto", "Dividendo", "Estado"};
        for (String h : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        // Filas
        for (SolicitudCredito s : solicitudes) {
            table.addCell(centrado(String.valueOf(s.getId())));
            table.addCell(texto(s.getUsuario().getNombre() + " " + s.getUsuario().getApellido()));
            table.addCell(texto(s.getPropiedad().getTitulo()));
            table.addCell(centrado(formatMonto(s.getMontoSolicitado())));
            table.addCell(centrado(formatMonto(s.getDividendoMensual())));
            table.addCell(centrado(s.getEstado().name()));
        }

        document.add(table);

        // Total
        BigDecimal total = solicitudes.stream()
                .map(SolicitudCredito::getMontoSolicitado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        document.add(new Paragraph("Total solicitado: " + formatMonto(total))
                .setFontSize(11)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(15));

        document.close();
        return baos.toByteArray();
    }

    // ─── EXCEL ──────────────────────────────────────────────────────────────

    public byte[] exportarSolicitudesExcel(EstadoSolicitud estado) throws IOException {
        List<SolicitudCredito> solicitudes = estado != null
                ? solicitudRepository.findByEstado(estado, org.springframework.data.domain.Pageable.unpaged()).getContent()
                : solicitudRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Solicitudes");

            // Estilo encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Estilo monto
            CellStyle montoStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            montoStyle.setDataFormat(format.getFormat("#,##0.00"));

            // Encabezados
            String[] headers = {"ID", "Cliente", "Email", "Propiedad", "Monto Solicitado",
                    "Dividendo Mensual", "Plazo (años)", "Tasa (%)", "CAE (%)", "Estado", "Fecha"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            int rowNum = 1;
            for (SolicitudCredito s : solicitudes) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getUsuario().getNombre() + " " + s.getUsuario().getApellido());
                row.createCell(2).setCellValue(s.getUsuario().getEmail());
                row.createCell(3).setCellValue(s.getPropiedad().getTitulo());

                org.apache.poi.ss.usermodel.Cell montoCell = row.createCell(4);
                montoCell.setCellValue(s.getMontoSolicitado().doubleValue());
                montoCell.setCellStyle(montoStyle);

                org.apache.poi.ss.usermodel.Cell divCell = row.createCell(5);
                divCell.setCellValue(s.getDividendoMensual().doubleValue());
                divCell.setCellStyle(montoStyle);

                row.createCell(6).setCellValue(s.getPlazoAnos());
                row.createCell(7).setCellValue(s.getTasaInteres().multiply(BigDecimal.valueOf(100)).doubleValue());
                row.createCell(8).setCellValue(s.getCae().multiply(BigDecimal.valueOf(100)).doubleValue());
                row.createCell(9).setCellValue(s.getEstado().name());
                row.createCell(10).setCellValue(
        s.getCreatedAt() != null ? s.getCreatedAt().format(FMT) : "");
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    private Cell centrado(String texto) {
        return new Cell().add(new Paragraph(texto).setTextAlignment(TextAlignment.CENTER));
    }

    private Cell texto(String texto) {
        return new Cell().add(new Paragraph(texto));
    }

    private String formatMonto(BigDecimal monto) {
        if (monto == null) return "$0";
        return String.format("$%,.0f", monto);
    }
}