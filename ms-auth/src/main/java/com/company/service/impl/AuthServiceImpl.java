package com.company.service.impl;

import com.company.client.UserClient;
import com.company.exception.InputNotValidException;
import com.company.exception.UnauthorizedException;
import com.company.jwt.JwtUtil;
import com.company.model.dto.UserDto;
import com.company.model.dto.request.AuthRequest;
import com.company.model.dto.request.RefreshTokenRequest;
import com.company.model.dto.request.RegistrationRequest;
import com.company.model.dto.response.AuthResponse;
import com.company.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.company.exception.constant.ErrorCode.INPUT_NOT_VALID;
import static com.company.exception.constant.ErrorCode.UNAUTHORIZED;
import static com.company.exception.constant.ErrorMessage.INPUT_NOT_VALID_MESSAGE;
import static com.company.exception.constant.ErrorMessage.UNAUTHORIZED_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegistrationRequest request) {
        log.info("Registering user, userEmail {}", request.getEmail());
        userClient.register(request);
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        log.info("Login user, userEmail {}", request.getEmail());
        UserDto userDto = userClient.getUserByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), userDto.getHashedPassword())) {
            throw new InputNotValidException(INPUT_NOT_VALID_MESSAGE, INPUT_NOT_VALID);
        }

        String accessToken = jwtUtil.generateAccessToken(userDto);
        String refreshToken = jwtUtil.generateRefreshToken(userDto);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        log.info("Refreshing token");
        if (!jwtUtil.isRefreshTokenValid(request.getRefreshToken(), request.getUserEmail())) {
            log.warn("Refresh token is not valid");
            throw new UnauthorizedException(UNAUTHORIZED_MESSAGE, UNAUTHORIZED);
        }
        log.info("Getting user info from userMS");
        String userEmail = jwtUtil.extractUserEmail(request.getRefreshToken());
        UserDto userDto = userClient.getUserByEmail(userEmail);
        String refreshToken = jwtUtil.generateRefreshToken(userDto);
        String accessToken = jwtUtil.generateAccessToken(userDto);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
