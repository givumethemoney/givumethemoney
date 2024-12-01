package com.hey.givumethemoney.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hey.givumethemoney.domain.Role;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {
    // JWT 서명 및 검증에 사용되는 비밀 키
    private final SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters long");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createJwt(String email, String role, Long expiredMs) {
        try {
            if (expiredMs == null) {
                expiredMs = 1000L * 60 * 60 * 10; // 기본 10시간
            }
            System.out.println("JWT 생성 시작");
            String jwt = Jwts.builder()
                    .claim("email", email)
                    .claim("role", role)
                    .setIssuedAt(new Date(System.currentTimeMillis())) // 발급 시간
                    .setExpiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간
                    .signWith(secretKey)
                    .compact();
            System.out.println("JWT 생성 성공: " + jwt);
            return jwt;
        } catch (Exception e) {
            System.out.println("JWT 생성 중 예외 발생: " + e.getMessage());
            return null;
        }
    }
    

    // JWT를 검증하여 유효한 경우 Claims를 반환
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

    public Role getRole(String token) {
        Claims claims = validateJwt(token);
        String roleString = claims.get("role", String.class);
        return Role.valueOf(roleString); // 문자열을 Enum으로 변환
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
