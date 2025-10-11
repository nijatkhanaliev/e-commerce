package com.company.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class AccountResponseDTO {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private String status;
}