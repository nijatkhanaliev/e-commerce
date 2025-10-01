package com.company.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
    @NotNull(message = "ACCOUNT.REQUEST.userId cannot be null")
    private Long userId;
}
