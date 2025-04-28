package com.niyiment.report.exporter.impl;

import com.niyiment.report.exporter.ReportExporter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class ExcelReportExporter implements ReportExporter {
    private final DataSource dataSource;

    @Override
    public void export(List<String> headers, Stream<Map<String, Object>> records, OutputStream outputStream) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");

        AtomicInteger rowIndex = new AtomicInteger();
        Row headerRow = sheet.createRow(rowIndex.getAndIncrement());
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }

        records.forEach(record -> {
            Row row = sheet.createRow(rowIndex.getAndIncrement());
            int cellIdx = 0;
            for (var value : record.values()) {
                row.createCell(cellIdx++).setCellValue(value != null ? value.toString() : "");
            }
        });

        workbook.write(outputStream);
        workbook.close();
    }


    @Override
    public String getFormat() {
        return "excel";
    }
}
