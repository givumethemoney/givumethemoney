package com.hey.givumethemoney.jwt;

import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.dto.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


@Component
@WebFilter("/*")
// 사용자가 요청을 보낼 때, 
// 요청 헤더에 포함된 JWT 토큰을 검사하여 인증 정보를 확인하고, 
// 유효한 경우 인증 상태를 설정합니다.
public class JWTFilter extends OncePerRequestFilter {

    // JWT 생성 및 검증 관련 메서드가 구현되어 있는 유틸리티 클래스
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("JWTFilter is processing request: " + request.getRequestURI());

        // 쿠키에서 JWT 토큰을 찾음
        String token = resolveTokenFromCookies(request);

        // 쿠키에 JWT 토큰이 없으면 인증을 하지 않고 다음 필터로 전달
        if (token == null || token.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        //request에서 Authorization 헤더를 찾음
        // String authorization = request.getHeader("Authorization");

        //Authorization 헤더 검증
        // if (authorization == null || !authorization.startsWith("Bearer ")) {
        //     System.out.println("token null");
        //     // 헤더 값이 없거나 Bearer로 시작하지 않으면 요청을 다음 필터로 전달
        //     filterChain.doFilter(request, response);
        //     return;
        // }

        //Bearer 부분 제거 후 순수 토큰만 획득
        // String token = authorization.split(" ")[1];

            //토큰 소멸 시간 검증
            try {
                // 토큰 검증 및 Claims 가져오기
                // JWT의 유효성을 확인하고, 내부 데이터를 Claims 객체로 반환.
                // Claims는 토큰에 저장된 데이터(email, role 등)를 포함.
                Claims claims = jwtUtil.validateJwt(token);
                System.out.println("Claims: " + claims);

                // 토큰이 만료되었는지 확인
                if (jwtUtil.isExpired(token)) {
                    System.out.println("token expired");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token has expired. Please log in again.");
                    return;
                }

                String email = jwtUtil.getEmail(token);
                com.hey.givumethemoney.domain.Role role = jwtUtil.getRole(token);
        
                // 사용자 도메인 생성 및 설정
                MemberDomain member = new MemberDomain();
                member.setEmail(email);// String -> Enum 변환
                member.setRole(role);

                // CustomUserDetails 생성
                CustomUserDetails customerUserDetails = new CustomUserDetails(member);

                // 권한 생성 및 SecurityContext 설정
                String authority = "ROLE_" + role.name();
                Authentication authToken = new UsernamePasswordAuthenticationToken(
                    customerUserDetails,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(authority)));
                
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("Authentication set for: " + email + " with role: " + role);

            } catch (ExpiredJwtException e) {
                System.out.println("Token is expired: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has expired. Please log in again.");
                return;
            }
            filterChain.doFilter(request, response);
        
    }

    // 쿠키에서 JWT 토큰을 추출하는 메서드
    private String resolveTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
