package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.dto.CustomUserDetails;
import com.hey.givumethemoney.service.CustomUserService;
import com.hey.givumethemoney.service.MemberService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class UserController {

    private final MemberService memberService;
    private final CustomUserService customUserService;

    public UserController(MemberService memberService, CustomUserService customUserService) {
        this.memberService = memberService;
        this.customUserService = customUserService;
    }

    @GetMapping("/index")
    public String index(Model model) {
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            // 유저 정보 전달
            String email = customUserService.getEmail();
            if (email.equals("anonymous")) {
                model.addAttribute("user", null);
            }
            else {
                MemberDomain member = memberService.findByEmail(email);
                model.addAttribute("user", member);
                System.out.println("userController의 member: " + member.getEmail());
                System.out.println("userController의 member: " + member.getRole());

                // 역할에 따라 다른 화면을 반환
                return "redirect:/company?email=" + member.getEmail() + "&role=" + member.getRole();
            }
            
        }

        return "login"; // 인증되지 않은 사용자는 로그인 화면으로
    }


    @GetMapping("/admin")
    public String admin(@RequestParam("email") String email, @RequestParam("role") String role, Model model) {
        System.out.println("/admin 요청 처리 시작\n");
        
        // 모델에 user 정보 추가
        MemberDomain user = memberService.findByEmail(email);
        if (user != null) {
            model.addAttribute("user", user);
            System.out.println("User: " + user);  // 여기서 user 객체를 확인
        }

        return "adminPage";  
    }


    @GetMapping("/company")
    public String company(@RequestParam("email") String email, @RequestParam("role") String role, Model model) {
        System.out.println("/company요청 처리 시작\n");
        
        // 모델에 user 정보 추가
        MemberDomain user = memberService.findByEmail(email);
        if (user != null) {
            model.addAttribute("user", user);
            System.out.println("User: " + user);  // 여기서 user 객체를 확인
        }

        return "companyPage";  
    }

}