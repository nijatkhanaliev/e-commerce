package com.company.jwt;

import com.company.cache.RedisService;
import com.company.model.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${application.jwt.secret-key-str}")
    private String secretKeyStr;

    @Value("${application.jwt.access-expiration-ms}")
    private Long accessExpirationMs;

    @Value("${application.jwt.refresh-expiration-ms}")
    private Long refreshExpirationMs;

    private final RedisService redisService;

    private SecretKey secretKey;

    @PostConstruct
    void init() {
        byte[] decodedSecretKey = Decoders.BASE64.decode(secretKeyStr);
        this.secretKey = Keys.hmacShaKeyFor(decodedSecretKey);
    }

    public String generateAccessToken(UserDto user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getUserRole());
        String accessToken = createToken(user.getEmail(), claims, accessExpirationMs);
        redisService.saveAccessToken(accessToken, user.getEmail(), accessExpirationMs);

        return accessToken;
    }

    public String generateRefreshToken(UserDto user) {
        String refreshToken = createToken(user.getEmail(), null, refreshExpirationMs);
        redisService.saveRefreshToken(refreshToken, user.getEmail(),refreshExpirationMs);

        return refreshToken;
    }

    private String createToken(String userEmail, Map<String, Object> claims, Long expirationMs) {
        return Jwts.builder()
                .subject(userEmail)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }


    public String extractUserEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaim(token);

        return resolver.apply(claims);
    }

    private Claims extractAllClaim(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isRefreshTokenValid(String token, String userEmail) {
        return Objects.equals(extractUserEmail(token), userEmail) &&
                isTokenExpired(token) &&
                redisService.isRefreshTokenValid(userEmail, token);
    }

    private boolean isTokenExpired(String token) {
        return !extractClaim(token, Claims::getExpiration).before(new Date());
    }

}
