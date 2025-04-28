package com.niyiment.report.exporter.impl;

import com.niyiment.report.exception.ReportException;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.niyiment.report.exporter.ReportExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;


@Component
@RequiredArgsConstructor
public class PdfReportExporter implements ReportExporter {
    private final DataSource dataSource;

    @Override
    public void export(List<String> headers, Stream<Map<String, Object>> records, OutputStream outputStream) throws Exception {
        try(PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf)){
            document.add(new Paragraph("Report"));

            document.add(new Paragraph(String.join(" | ", headers)));
            document.add(new Paragraph("----------------------------------------------------"));

            records.forEach(record -> {
                try {
                    document.add(new Paragraph(record.values().stream()
                            .map(val -> val != null ? val.toString() : "")
                            .collect(Collectors.joining(" | "))));
                } catch (ReportException e) {
                    throw new RuntimeException(e.getMessage());
                }
            });
        }
    }

    @Override
    public String getFormat() {
        return "pdf";
    }
}

