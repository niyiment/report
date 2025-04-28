package com.niyiment.report.exporter;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface ReportExporter {

    void export(List<String> headers, Stream<Map<String, Object>> records, OutputStream outputStream) throws Exception;

    String getFormat();
}
