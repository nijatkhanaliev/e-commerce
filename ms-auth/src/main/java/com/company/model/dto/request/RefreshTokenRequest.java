package com.company.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
    private String userEmail;
    private String refreshToken;
}
