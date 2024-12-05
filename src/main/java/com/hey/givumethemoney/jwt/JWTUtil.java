package com.hey.givumethemoney.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmail(String token) {
        return validateJwt(token).get("email").toString();
    }

    public Role getRole(String token) {
        String role = (String) validateJwt(token).get("role");
        return Role.valueOf(role.replace("ROLE_", ""));
    }
    

    public boolean isExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (claims.getExpiration() == null) {
                System.out.println("Expiration date is null. Token is considered expired.");
                return true; // 만료로 간주
            }

            return claims.getExpiration().before(new Date());
        }
        catch (ExpiredJwtException e) {
            return true;
        }
    }
}
