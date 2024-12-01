package com.hey.givumethemoney.jwt;

import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

// LoginFilter의 두 메서드(attemptAuthentication과 successfulAuthentication)는 
// Spring Security의 필터 체인에 의해 자동으로 순차적으로 실행!!!!
// 사용자가 로그인 요청을 보내면, Spring Security는 
// 로그인 필터의 흐름에 따라 각 메서드를 자동으로 호출

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
                       MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            System.out.println("로그인 필터의 attemptAuthentication 실행!!!!\n\n\n");
            String email = request.getParameter("email");
            System.out.println("email: " + email);
            String password = request.getParameter("password");

            // 데이터베이스에서 사용자 조회
            MemberDomain member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("User not found with email: " + email));

            System.out.println("loginFilter: 데이터베이스에서 사용자 조회 완료");

            // 비밀번호 검증
            if (!passwordEncoder.matches(password, member.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }
            System.out.println("loginFilter: 비밀번호 검증 완료");

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password, null);
//            System.out.println("Principal during token creation: " + authToken.getPrincipal());
            return authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage()) {};
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain chain, Authentication authentication) {

        System.out.println("로그인 필터의 successfulAuthentication 실행!!!\n\n\n");

        //email 추출
        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER");
        System.out.println("Role: " + role);

        //JWT 생성
        System.out.println("loginFilter [successfulAuthentication]: jwt 토큰 생성을 위해 jwtUtil의 createJwt 호출\n\n");
        String token = jwtUtil.createJwt(email, role, 60 * 60 * 10L);

        // 로그 추가
        System.out.println("Authentication successful for: " + email + " with role: " + role);

        // 역할에 따라 리다이렉션 설정
        try {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"token\": \"" + token + "\", \"email\": \"" + email + "\", \"role\": \"" + role + "\"}");

            // 로그인 성공 후 리다이렉트 (예: /index 페이지로)
            response.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }
}