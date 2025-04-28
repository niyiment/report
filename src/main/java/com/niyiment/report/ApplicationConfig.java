package com.niyiment.report;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Initialize sample report configurations
     */
//    @Bean
//    public CommandLineRunner initReportConfigs(ReportConfigService reportConfigService) {
//        return args -> {
//            // Customer List Report
//            reportConfigService.registerReportConfig("customer-list", ReportConfig.builder()
//                    .name("Customer List")
//                    .description("List of all customers with basic information")
//                    .query("SELECT id, name, email, phone, registration_date FROM customers")
//                    .fields(List.of("id", "name", "email", "phone", "registration_date"))
//                    .exportFormat("pdf")
//                    .filenameTemplate("customer_list_{date}.pdf")
//                    .pageSize(500)
//                    .build());
//
//            // Customer Statistics Report
//            reportConfigService.registerReportConfig("customer-stats", ReportConfig.builder()
//                    .name("Customer Statistics")
//                    .description("Monthly customer registration statistics")
//                    .query("SELECT DATE_TRUNC('month', registration_date) as month, COUNT(*) as count " +
//                            "FROM customers GROUP BY DATE_TRUNC('month', registration_date) ORDER BY month")
//                    .fields(List.of("month", "count"))
//                    .exportFormat("excel")
//                    .filenameTemplate("customer_stats_{date}.xlsx")
//                    .pageSize(100)
//                    .build());
//
//            // Customer Search Report
//            reportConfigService.registerReportConfig("customer-search", ReportConfig.builder()
//                    .name("Customer Search")
//                    .description("Customizable customer search report")
//                    .query("SELECT * FROM customers")
//                    .fields(List.of("id", "name", "email", "phone", "registration_date"))
//                    .exportFormat("csv")
//                    .filenameTemplate("customer_search_{timestamp}.csv")
//                    .pageSize(1000)
//                    .build());
//        };
//    }
}
