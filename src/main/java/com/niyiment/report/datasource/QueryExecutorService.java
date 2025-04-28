package com.niyiment.report.datasource;

import java.util.Map;
import java.util.stream.Stream;

public interface QueryExecutorService {
    Stream<Map<String, Object>> executeQuery(String query, Map<String, Object> params);
}
