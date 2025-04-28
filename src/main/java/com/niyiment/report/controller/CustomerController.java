package com.niyiment.report.controller;

import com.niyiment.report.dto.CustomerRequest;
import com.niyiment.report.dto.CustomerResponse;
import com.niyiment.report.model.Customer;
import com.niyiment.report.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Slf4j
@RequestMapping("/api/customers")
@RestController
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/export-zip")
    public ResponseEntity<StreamingResponseBody> downloadCustomerReportsZip(@RequestBody Map<String, Object> filters) {
        String filename = "customer_reports_" + LocalDate.now() + ".zip";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(customerService.exportCustomerReportAsZip(filters));
    }

    @PostMapping("/export/{format}")
    public ResponseEntity<StreamingResponseBody> downloadCustomerReport(@PathVariable String format,
                                                                        @RequestBody Map<String, Object> filters) {
        String filename = "customer_report_" + LocalDate.now() + "." + format;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(getContentType(format))
                .body(customerService.exportCustomerReport(format, filters));
    }

    private static class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Customer.builder()
                    .id(rs.getLong("id"))
                    .name(rs.getString("name"))
                    .email(rs.getString("email"))
                    .phone(rs.getString("phone"))
                    .registrationDate(rs.getDate("registration_date").toLocalDate())
                    .build();
        }
    }

    @GetMapping("/jdbc")
    public List<Customer> getAllCustomersJdbc() {
        return jdbcTemplate.query(
                "SELECT id, name, email, phone, registration_date FROM customers LIMIT 100",
                new CustomerRowMapper()
        );
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers(){
        List<CustomerResponse> result = customerService.getAllCustomers();

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> saveCustomer(@RequestBody CustomerRequest request ) {
        CustomerResponse result = customerService.saveCustomer(request);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/dummy")
    public ResponseEntity<String> dummyCustomer(){
       int recordSize  = customerService.dummyCustomer();

        return ResponseEntity.ok("Inserted " + recordSize  + " dummy customers.");
    }

    private MediaType getContentType(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "excel" -> MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            case "csv" -> MediaType.TEXT_PLAIN;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}
