package com.hey.givumethemoney.jwt;

import com.hey.givumethemoney.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import java.util.Date;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    public JWTFilter(JWTUtil jwtUtil, MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            System.out.println("token null");
            filterChain.doFilter(request, response);
            return;
        }

        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];
        System.out.println("Token received: " + token);

        //토큰 소멸 시간 검증
        try {
            // 토큰 검증 및 Claims 가져오기
            Claims claims = jwtUtil.validateJwt(token);
            System.out.println("Claims: " + claims);

            System.out.println("Expiration time: " + claims.getExpiration());
            System.out.println("Current time: " + new Date());

            if (jwtUtil.isExpired(token)) {
                System.out.println("token expired");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                filterChain.doFilter(request, response);
                return;
            }
            //토큰에서 email과 role 획득
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);
            System.out.println("Email from token: " + email);
            System.out.println("Role from token: " + role);


            Authentication authToken = new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (ExpiredJwtException e) {
            System.out.println("Token is expired: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token has expired. Please log in again.");
            return;

        } catch (Exception e) {
            System.out.println("Error during token validation: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token.");
            return;
        }
        filterChain.doFilter(request, response);
    }
}


//        MemberDomain member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));


        // 데이터베이스에서 사용자 정보 조회
//        MemberDomain memberDomain = memberService.findByEmail(email);

//        // 사용자 정보 기반으로 CustomUserDetails 생성
//        CustomUserDetails customUserDetails = new CustomUserDetails(memberDomain);
//
//        // Spring Security 인증 토큰 생성 및 SecurityContext에 등록
//        Authentication authToken = new UsernamePasswordAuthenticationToken(
//                customUserDetails, null, customUserDetails.getAuthorities()
//        );

        // SecurityContext에 인증 객체 설정
//        SecurityContextHolder.getContext().setAuthentication(authToken);


//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        return request.getServletPath().matches("/login|/join|/public.*");
//    }
