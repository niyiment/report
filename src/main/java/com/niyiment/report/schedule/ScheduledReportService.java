package com.niyiment.report.schedule;

import com.niyiment.report.datasource.QueryExecutorService;
import com.niyiment.report.dto.DynamicQueryRequest;
import com.niyiment.report.exception.ReportException;
import com.niyiment.report.exporter.ReportExporter;
import com.niyiment.report.query.CustomerReportQueryProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ScheduledReportService {
    private final CustomerReportQueryProvider customerReportQueryProvider;
    private final QueryExecutorService queryExecutorService;

    private final Map<String, ReportExporter> exporters;
    private static final  Map<String, String> FORMAT_TO_FILENAME = Map.of(
            "pdf", "customer_report.pdf",
            "excel", "customer_report.xlsx",
            "csv", "customer_report.csv"
    );

    public ScheduledReportService(CustomerReportQueryProvider customerReportQueryProvider,
                                  QueryExecutorService queryExecutorService,
                                  List<ReportExporter> exporterList) {
        this.customerReportQueryProvider = customerReportQueryProvider;
        this.queryExecutorService = queryExecutorService;
        this.exporters = exporterList.stream()
                .collect(Collectors.toMap(
                        e -> e.getFormat().toLowerCase(),
                        Function.identity()
                ));
    }

    @Scheduled(cron = "0 0 6 * * *")
    public void generateDailyCustomerReport() {
        try {
            DynamicQueryRequest request = DynamicQueryRequest.builder()
                    .filters(Map.of())
                    .sortBy("registration_date")
                    .sortDirection("DESC")
                    .page(0)
                    .size(10000)
                    .build();

            PipedInputStream pipedInputStream = new PipedInputStream();
            PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
            CompletableFuture.runAsync(() -> {
                try( ZipOutputStream zipOutputStream = new ZipOutputStream(pipedOutputStream)){
                    for (Map.Entry<String, String> entry : FORMAT_TO_FILENAME.entrySet()) {
                        String format = entry.getKey();
                        String filename = entry.getValue();

                        ReportExporter reportExporter = findExporter(format);
                        zipOutputStream.putNextEntry(new ZipEntry(filename));

                        String query = customerReportQueryProvider.buildQuery(request);
                        Map<String, Object> parameters = customerReportQueryProvider.getQueryParams(request);
                        List<String> headers = customerReportQueryProvider.getColumnHeaders();

                        try(Stream<Map<String, Object>> dataStream = queryExecutorService.executeQuery(query, parameters)) {
                            reportExporter.export(headers, dataStream, zipOutputStream);
                        } catch (Exception e) {
                            throw new ReportException(e.getMessage());
                        }

                        zipOutputStream.closeEntry();
                    }
                    zipOutputStream.finish();;
                } catch (Exception e) {

                }
            });

        } catch (Exception e) {
            throw new ReportException(e.getMessage());
        }
    }

    private ReportExporter findExporter(String format) {
        ReportExporter exporter = exporters.get(format.toLowerCase());
        if (exporter == null) {
            throw new IllegalArgumentException("Unsupported format: " + format + ". Available: " + exporters.keySet());
        }
        return exporter;
    }
}
