package com.hey.givumethemoney.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    private final SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        System.out.println("JWT_SECRET: " + secret); // 로그 출력
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createJwt(String email, String role, Long expiredMs) {
        if (expiredMs == null) {
            expiredMs = 1000L * 60 * 60 * 10; // 기본 10시간
        }
        return Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간
                .signWith(secretKey)
                .compact();
    }

    public Claims validateJwt(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public String getEmail(String token) {
        Claims claims = validateJwt(token);
        return claims.get("email", String.class);
    }

    public String getRole(String token) {
        Claims claims = validateJwt(token);
        return claims.get("role", String.class); // ROLE_ 없이 반환
    }

    public boolean isExpired(String token) {
        Claims claims = validateJwt(token);
//        System.out.println("Claims: " + claims);
//        System.out.println("Expiration time from token: " + claims.getExpiration());
//        System.out.println("Current time: " + new Date());

        Date expiration = claims.getExpiration();

        if (expiration == null) {
            System.out.println("Expiration date is null. Token is considered expired.");
            return true; // 만료로 간주
        }
        return expiration.before(new Date());
    }
}
