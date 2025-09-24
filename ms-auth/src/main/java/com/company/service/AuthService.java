package com.company.service;

import com.company.model.dto.request.AuthRequest;
import com.company.model.dto.request.RefreshTokenRequest;
import com.company.model.dto.request.RegistrationRequest;
import com.company.model.dto.response.AuthResponse;

public interface AuthService {

    void register(RegistrationRequest request);

    AuthResponse login(AuthRequest request);

    AuthResponse refresh(RefreshTokenRequest request);
}
