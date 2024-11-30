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
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            // 데이터베이스에서 사용자 조회
            MemberDomain member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("User not found with email: " + email));

            // 비밀번호 검증
            if (!passwordEncoder.matches(password, member.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }

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

        //email, role 추출
        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER");

        //JWT 생성
        String token = jwtUtil.createJwt(email, role, 60 * 60 * 10L);

        // 로그 추가
        System.out.println("Authentication successful for: " + email + " with role: " + role);

        // 역할에 따라 리다이렉션 설정
        try {
            response.setHeader("Authorization", "Bearer " + token);
            String redirectUrl = "ROLE_ADMIN".equals(role) ? "/admin" : "/company";
            System.out.println("Redirecting to: " + redirectUrl);
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        response.setStatus(401);
    }
}