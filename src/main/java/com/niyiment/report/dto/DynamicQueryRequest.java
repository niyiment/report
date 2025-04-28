package com.niyiment.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DynamicQueryRequest {
    private Map<String, Object> filters;
    private String sortBy;
    private String sortDirection;
    private Integer page;
    private Integer size;
}
