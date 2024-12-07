package com.hey.givumethemoney.Config;

import com.hey.givumethemoney.jwt.JWTFilter;
import com.hey.givumethemoney.jwt.JWTUtil;
import com.hey.givumethemoney.repository.MemberRepository;

import com.hey.givumethemoney.jwt.LoginFilter;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTFilter jwtFilter;
    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(JWTFilter jwtFilter, JWTUtil jwtUtil, MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.jwtFilter = jwtFilter;
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        // LoginFilter 생성 및 설정
        LoginFilter loginFilter = new LoginFilter(authenticationManager, jwtUtil, memberRepository, passwordEncoder);

        http
                .csrf(csrf -> csrf.disable()) // RESTful API에서는 보통 CSRF가 필요하지 않으므로
                .authorizeHttpRequests(auth -> auth
                        // 특정 URL은 인증 없이 접근 가능(permitAll())
                        .requestMatchers("/login", "/join", "/", "/css/**", "/js/**", "/images/**", "/image/*", "/error", "/webfonts/*", "/chatbot/**").permitAll()
                        .requestMatchers("/detail/*", "/payments", "/pay", "/success","/receipts/**", "/receiptList/*","/donationList", "/donationList/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login-fail").permitAll()
                        // 특정 역할이 있어야만 접근 가능한 URL(hasRole("ROLE"))
                        .requestMatchers("/applicationList/*", "/application/agree", "/application/write", "/application/edit",
                                "waitingList/*", "/endList/*", "/logout").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/application/submit", "/application/submitEdit", "/api/files").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/receipt/submit").hasRole("COMPANY")
                        .requestMatchers(HttpMethod.GET, "/receiptPopup").hasRole("COMPANY")
                        .requestMatchers("/application/confirm/*", "/application/reject/*").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증이 필요
                        .anyRequest().authenticated()
                )
                // LoginFilter: 로그인 요청을 처리. 이 필터는 사용자 인증 후 JWT를 생성
                // LoginFilter는 일반적으로 POST 요청만 처리하는 필터임
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
                // JWTFilter: 요청에 포함된 JWT 토큰을 검증하고, 인증된 사용자 정보를 설정
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        // SessionCreationPolicy.STATELESS: 세션을 생성하거나 유지하지 않음
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // /login 페이지를 커스텀 로그인 페이지로 사용
                // 이 페이지는 누구나 접근 가능
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successForwardUrl("/")
                    )
                // /logout URL로 로그아웃
                // 로그아웃 성공 후 /로 리다이렉트
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler((request, reponse, authentication) -> {
                            HttpSession session = request.getSession();
                            if (session != null) {
                                session.invalidate(); // 세션 무효화
                            }
                        })
                        .deleteCookies("jwt_token")
                        .logoutSuccessUrl("/")
                        .permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
