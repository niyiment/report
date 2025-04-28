package com.niyiment.report.service.impl;

import com.github.javafaker.Faker;
import com.niyiment.report.dto.CustomerRequest;
import com.niyiment.report.dto.CustomerResponse;
import com.niyiment.report.dto.DynamicQueryRequest;
import com.niyiment.report.model.Customer;
import com.niyiment.report.query.CustomerReportQueryProvider;
import com.niyiment.report.repository.CustomerRepository;
import com.niyiment.report.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final DynamicReportService dynamicReportService;
    private final CustomerReportQueryProvider customerReportQueryProvider;

    private static final int FETCH_SIZE = 1000;
    private static final String QUERY = """
                SELECT name, email, phone, registration_date
                FROM customers
                WHERE (:name IS NULL OR name = ?)
                  AND (:fromDate IS NULL OR registration_date >= ?)
                  AND (:toDate IS NULL OR registration_date <= ?)
                """;

    @Override
    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customerList = customerRepository.findAll();

        return customerList.stream().map(this::toDto).toList();
    }

    @Override
    public CustomerResponse saveCustomer(CustomerRequest dto) {
        Customer customer = toEntity(dto);
        customerRepository.save(customer);

        return toDto(customer);
    }

    @Override
    public int dummyCustomer() {
        Faker faker = new Faker();
        Random random = new Random();
        int recordSize = 10_000;
        List<Customer> customerList = new ArrayList<>();

        for (int i = 0; i < recordSize; i++) {

            Customer customer = Customer.builder()
                    .name(faker.name().fullName())
                    .email(faker.internet().emailAddress())
                    .phone(faker.phoneNumber().phoneNumber())
                    .registrationDate(LocalDate.now().minusDays(random.nextInt(365)))
                    .build();

            customerList.add(customer);
            if (customerList.size() == 1000) {
                customerRepository.saveAll(customerList);
                customerList.clear();
            }
        }
        if (!customerList.isEmpty()) {
            customerRepository.saveAll(customerList);
        }

        return recordSize;
    }

    private Customer toEntity(CustomerRequest dto) {
        return Customer.builder()
                .name(dto.name())
                .email(dto.email())
                .phone(dto.phone())
                .registrationDate(dto.registrationDate())
                .build();
    }

    private CustomerResponse toDto(Customer customer){
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .registrationDate(customer.getRegistrationDate())
                .build();
    }

    @Override
    public StreamingResponseBody exportCustomerReport(String format, DynamicQueryRequest queryRequest) {
        return dynamicReportService.generateReport(format, customerReportQueryProvider, queryRequest);
    }

    @Override
    public StreamingResponseBody exportCustomerReportAsZip(DynamicQueryRequest queryRequest) {
        Map<String, String> formatToFilename = Map.of(
                "pdf", "customer_report.pdf",
                "excel", "customer_report.xlsx",
                "csv", "customer_report.csv"
        );

        return dynamicReportService.generateReportZip(formatToFilename, customerReportQueryProvider, queryRequest);
    }

}
