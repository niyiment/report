package com.niyiment.report.service;

import com.niyiment.report.dto.CustomerRequest;
import com.niyiment.report.dto.CustomerResponse;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface CustomerService {

    List<CustomerResponse> getAllCustomers();

    CustomerResponse saveCustomer(CustomerRequest dto);

    StreamingResponseBody exportCustomerReport(String format, Map<String, Object> filters);

    StreamingResponseBody exportCustomerReportAsZip(Map<String, Object> filters);

    int dummyCustomer();
}
