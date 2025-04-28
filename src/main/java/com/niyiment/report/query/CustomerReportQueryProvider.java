package com.niyiment.report.query;

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
    public String buildQuery(Map<String, Object> filters) {
        StringBuilder query = new StringBuilder("SELECT name, email, phone, registration_date FROM customers WHERE 1=1");

        if (filters.containsKey("startDate")) {
            query.append(" AND registration_date >= :startDate");
        }
        if (filters.containsKey("endDate")) {
            query.append(" AND registration_date <= :endDate");
        }
        if (filters.containsKey("name")) {
            query.append(" AND LOWER(name) LIKE LOWER(:name)");
        }

        return query.toString();
    }


    @Override
    public Map<String, Object> getQueryParams(Map<String, Object> filters) {
        Map<String, Object> params = new HashMap<>();
        if (filters.containsKey("startDate")) {
            try {
                String startDateStr = (String) filters.get("startDate");
                LocalDate startDate = LocalDate.parse(startDateStr);
                params.put("startDate", Date.valueOf(startDate));
            } catch (DateTimeParseException e) {
                log.error("Invalid startDate format: {}", filters.get("startDate"));
                throw new IllegalArgumentException("Invalid startDate format");
            }
        }
        if (filters.containsKey("endDate")) {
            try {
                String endDateStr = (String) filters.get("endDate");
                LocalDate endDate = LocalDate.parse(endDateStr);
                params.put("endDate", Date.valueOf(endDate));
            } catch (DateTimeParseException e) {
                log.error("Invalid endDate format: {}", filters.get("startDate"));
                throw new IllegalArgumentException("Invalid startDate format");
            }
        }
        if (filters.containsKey("name")) {
            params.put("name", "%" + filters.get("name") + "%");
        }

        if (params.isEmpty()) {
            log.warn("No filters provided; query will return all customers");
        }

        return params;
    }

    @Override
    public List<String> getColumnHeaders() {
        return List.of("Name", "Email", "Phone", "Registration Date");
    }
}
