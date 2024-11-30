package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.dto.MemberDTO;
import com.hey.givumethemoney.jwt.JWTUtil;
import com.hey.givumethemoney.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;

@Controller
@RequiredArgsConstructor
public class JoinController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    // 회원가입 화면 반환
    @GetMapping("/join")
    public String joinForm() {
        return "join";
    }

    // 회원가입 처리
    @PostMapping("/join")
    public String join(@Valid @ModelAttribute MemberDTO memberDTO, BindingResult bindingResult) {
        //유효성 검사에서 오류가 발생한 경우
        if (bindingResult.hasErrors()) {
            //회원가입 페이지로 리턴
            return "join";
        }

        // 비밀번호 암호화는 MemberService에서 처리
        memberService.join(memberDTO);

        // 회원가입 후 인덱스로 리다이렉트
        return "redirect:/";
    }
}