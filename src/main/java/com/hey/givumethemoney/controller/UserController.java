package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.dto.CustomUserDetails;
import com.hey.givumethemoney.service.CustomUserService;
import com.hey.givumethemoney.service.MemberService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class UserController {

    private final MemberService memberService;

    public UserController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/index")
    public String index(Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername(); // 이메일 가져오기
            String role = userDetails.getAuthorities().stream()
                          .findFirst()
                          .map(Object::toString)
                          .orElse("UNKNOWN");

            // 사용자 정보를 모델에 추가
            model.addAttribute("email", email);
            model.addAttribute("role", role);

            // 역할에 따라 다른 화면을 반환
            if (role.equals("ROLE_ADMIN")) {
                return "adminPage"; // 관리자 화면
            } else {
                return "userPage"; // 일반 사용자 화면
            }
        }

        return "login"; // 인증되지 않은 사용자는 로그인 화면으로
    }
}
