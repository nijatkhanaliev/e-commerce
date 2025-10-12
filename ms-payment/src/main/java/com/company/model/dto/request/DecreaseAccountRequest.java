package com.company.model.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class DecreaseAccountRequest {
    private BigDecimal balance;
}
