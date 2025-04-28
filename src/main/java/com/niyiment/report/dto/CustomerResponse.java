package com.niyiment.report.dto;

import lombok.Builder;

import java.time.LocalDate;


@Builder
public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate registrationDate
) {
}