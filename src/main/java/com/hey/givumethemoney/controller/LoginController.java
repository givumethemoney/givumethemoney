package com.hey.givumethemoney.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {


    // 생성자 주입
    @Autowired
    public LoginController() { }


    // 로그인 화면 반환
    @GetMapping("/login")
    public String loginForm() {
        return "login";
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
