package com.company.unit.constant;

import com.company.dao.entity.Account;
import com.company.model.dto.response.AccountResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.company.model.enums.AccountStatus.ACTIVE;

public interface AccountTestConstant {

    Account ACCOUNT = new Account(1L, 1L, BigDecimal.valueOf(200), ACTIVE,
            LocalDateTime.now(), LocalDateTime.now());

    AccountResponse ACCOUNT_RESPONSE = new AccountResponse(1L, 1L,
            BigDecimal.valueOf(200), ACTIVE);

}
