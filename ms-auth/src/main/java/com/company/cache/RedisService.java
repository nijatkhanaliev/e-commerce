package com.company.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveAccessToken(String token, String userEmail, Long accessDurationMs) {
        saveToken("accessToken", userEmail, token, accessDurationMs);
    }

    public void saveRefreshToken(String token, String userEmail, Long refreshDurationMs) {
        saveToken("refreshToken", userEmail, token, refreshDurationMs);
    }

    private void saveToken(String tokenType, String userEmail, String token, Long durationMs) {
        String key = tokenType + ":" + userEmail;
        redisTemplate.opsForValue().set(key, token, durationMs, TimeUnit.MILLISECONDS);
    }

    public boolean isRefreshTokenValid(String userEmail, String expectedValue) {
        String refreshToken = redisTemplate.opsForValue().get("refreshToken:" + userEmail);

        return Objects.equals(refreshToken, expectedValue);
    }

    public void invalidateTokens(String userEmail) {
        invalidateToken("accessToken", userEmail);
        invalidateToken("refreshToken", userEmail);
    }

    public void invalidateToken(String tokenType, String userEmail) {
        redisTemplate.delete(tokenType + ":" + userEmail);
    }

}