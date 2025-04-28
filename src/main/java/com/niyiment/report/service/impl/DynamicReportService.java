package com.niyiment.report.service.impl;

import com.niyiment.report.dto.DynamicQueryRequest;
import com.niyiment.report.exception.ReportException;
import com.niyiment.report.datasource.QueryExecutorService;
import com.niyiment.report.query.QueryProvider;
import com.niyiment.report.exporter.ReportExporter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DynamicReportService {
    private final QueryExecutorService queryExecutorService;
    private final Map<String, ReportExporter> exporters;

    public DynamicReportService(QueryExecutorService queryExecutorService, List<ReportExporter> exporterList) {
        this.queryExecutorService = queryExecutorService;
        this.exporters = exporterList.stream()
                .collect(Collectors.toMap(
                        e -> e.getFormat().toLowerCase(),
                        Function.identity()
                ));
    }

    public StreamingResponseBody generateReport(String format, QueryProvider queryProvider, DynamicQueryRequest queryRequest) {
        ReportExporter reportExporter = findExporter(format);

        String query = queryProvider.buildQuery(queryRequest);
        Map<String, Object> parameters = queryProvider.getQueryParams(queryRequest);
        List<String> headers = queryProvider.getColumnHeaders();
        Stream<Map<String, Object>> dataStreams = queryExecutorService.executeQuery(query, parameters);

        return outputStream -> {
            try (Stream<Map<String, Object>> stream = dataStreams) {
                reportExporter.export(headers, stream, outputStream);
            } catch (Exception e) {
                throw new ReportException(e.getMessage());
            }
        };
    }

    public StreamingResponseBody generateReportZip(Map<String, String> formatToFilename, QueryProvider queryProvider,
                                                   DynamicQueryRequest queryRequest) {
        return outputStream -> {
            try(ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)){
                for (Map.Entry<String, String> entry : formatToFilename.entrySet()) {
                    String format = entry.getKey();
                    String filename = entry.getValue();

                    ReportExporter reportExporter = findExporter(format);
                    zipOutputStream.putNextEntry(new ZipEntry(filename));

                    String query = queryProvider.buildQuery(queryRequest);
                    Map<String, Object> parameters = queryProvider.getQueryParams(queryRequest);
                    List<String> headers = queryProvider.getColumnHeaders();

                    try(Stream<Map<String, Object>> dataStream = queryExecutorService.executeQuery(query, parameters)) {
                        reportExporter.export(headers, dataStream, zipOutputStream);
                    } catch (Exception e) {
                        throw new ReportException(e.getMessage());
                    }

                    zipOutputStream.closeEntry();
                }
            }
        };
    }

    private ReportExporter findExporter(String format) {
        ReportExporter exporter = exporters.get(format.toLowerCase());
        if (exporter == null) {
            throw new IllegalArgumentException("Unsupported format: " + format + ". Available: " + exporters.keySet());
        }
        return exporter;
    }

}
