package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.jwt.JWTUtil;
import com.hey.givumethemoney.repository.MemberRepository;
import com.hey.givumethemoney.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    // 로그인 화면 반환
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // 로그인 폼 처리
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestParam("email") String email,
                                   @RequestParam("password") String password) {

        try {
            // 사용자 조회
            var memberDomain = memberService.findByEmail(email);

            // 비밀번호 검증
            if (!passwordEncoder.matches(password, memberDomain.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            // JWT 생성
            String token = jwtUtil.createJwt(email, memberDomain.getRole().toString(), 60 * 60 * 10L);

            // JWT 반환
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + token)
                    .body("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return ResponseEntity.status(HttpStatus.FOUND) // HTTP 302 리다이렉트
                .header("Location", "/") // 메인 페이지로 리다이렉트
                .build();
    }
}

