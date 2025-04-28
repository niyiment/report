package com.niyiment.report.query;

import java.util.List;
import java.util.Map;

public interface QueryProvider {
    String buildQuery(Map<String, Object> filters);
    Map<String, Object> getQueryParams(Map<String, Object> filters);
    List<String> getColumnHeaders();
}
