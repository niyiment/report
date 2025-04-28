package com.niyiment.report.query;

import com.niyiment.report.dto.DynamicQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CustomerReportQueryProvider  implements QueryProvider {
    @Override
    public String buildQuery(DynamicQueryRequest request) {
        StringBuilder query = new StringBuilder("SELECT name, email, phone, registration_date FROM customers WHERE 1=1");

       if (request.getFilters() != null) {
           if (request.getFilters().containsKey("startDate")) {
               query.append(" AND registration_date >= :startDate");
           }
           if (request.getFilters().containsKey("endDate")) {
               query.append(" AND registration_date <= :endDate");
           }
           if (request.getFilters().containsKey("name")) {
               query.append(" AND LOWER(name) LIKE LOWER(:name)");
           }
       }

        if (request.getSortBy() != null && request.getSortDirection() != null) {
            query.append(" ORDER BY ").append(request.getSortBy()).append(" ").append(request.getSortDirection());
        }

        return query.toString();
    }


    @Override
    public Map<String, Object> getQueryParams(DynamicQueryRequest request) {
        Map<String, Object> params = new HashMap<>();
        if (request.getFilters() != null) {
            if (request.getFilters().containsKey("startDate")) {
                try {
                    String startDateStr = (String) request.getFilters().get("startDate");
                    LocalDate startDate = LocalDate.parse(startDateStr);
                    params.put("startDate", Date.valueOf(startDate));
                } catch (DateTimeParseException e) {
                    log.error("Invalid startDate format: {}", request.getFilters().get("startDate"));
                    throw new IllegalArgumentException("Invalid startDate format");
                }
            }
            if (request.getFilters().containsKey("endDate")) {
                try {
                    String endDateStr = (String) request.getFilters().get("endDate");
                    LocalDate endDate = LocalDate.parse(endDateStr);
                    params.put("endDate", Date.valueOf(endDate));
                } catch (DateTimeParseException e) {
                    log.error("Invalid endDate format: {}", request.getFilters().get("startDate"));
                    throw new IllegalArgumentException("Invalid startDate format");
                }
            }
            if (request.getFilters().containsKey("name")) {
                params.put("name", "%" + request.getFilters().get("name") + "%");
            }

            if (params.isEmpty()) {
                log.warn("No filters provided; query will return all customers");
            }
        }

        return params;
    }

    @Override
    public List<String> getColumnHeaders() {
        return List.of("Name", "Email", "Phone", "Registration Date");
    }
}
