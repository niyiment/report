package com.niyiment.report.dto;


import lombok.Builder;

import java.time.LocalDate;


@Builder
public record CustomerRequest(
        String name,
        String email,
        String phone,
        LocalDate registrationDate
) {
}
