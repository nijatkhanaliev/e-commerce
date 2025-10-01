package com.company.model.dto.response;

import com.company.model.enums.AccountStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountResponse {
    private Long id;
    private Long userId;
    private BigDecimal balance;
    private AccountStatus status;
}
