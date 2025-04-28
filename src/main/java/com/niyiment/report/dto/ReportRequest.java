package com.niyiment.report.dto;


import lombok.Builder;

import java.util.Map;

@Builder
public record ReportRequest(
       String reportConfigId,
       Map<String, Object> filters,
       Map<String, Object> customParameters
) {
}
