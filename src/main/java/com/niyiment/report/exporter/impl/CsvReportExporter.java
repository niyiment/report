package com.niyiment.report.exporter.impl;

import com.niyiment.report.exporter.ReportExporter;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class CsvReportExporter implements ReportExporter {
    private final DataSource dataSource;

    @Override
    public void export(List<String> headers, Stream<Map<String, Object>> records, OutputStream outputStream) throws Exception {
        try(Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
            CSVWriter csvWriter = new CSVWriter(writer) ) {

            csvWriter.writeNext(headers.toArray(new String[0]));

            records.forEach(record -> {
                String[] row = record.values().stream()
                        .map(val -> val != null ? val.toString() : "")
                        .toArray(String[]::new);
                csvWriter.writeNext(row);
            });
        }
    }

    @Override
    public String getFormat() {
        return "csv";
    }
}
