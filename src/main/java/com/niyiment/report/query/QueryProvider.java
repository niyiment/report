package com.niyiment.report.query;

import com.niyiment.report.dto.DynamicQueryRequest;

import java.util.List;
import java.util.Map;

public interface QueryProvider {
    String buildQuery(DynamicQueryRequest request);
    Map<String, Object> getQueryParams(DynamicQueryRequest request);
    List<String> getColumnHeaders();
}
