package com.hey.givumethemoney.controller;


import com.hey.givumethemoney.domain.Role;
import jakarta.servlet.http.HttpSession;
import com.hey.givumethemoney.dto.MemberDTO;
import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // 추가된 import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    @GetMapping("/join")
    public String joinForm() {
        return "join";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute MemberDTO memberDTO) {
        System.out.println("MemberController.join");
        System.out.println("memberDTO = " + memberDTO);
        memberService.join(memberDTO);
        return "index";
    }

    // 로그인 처리
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/company")
    public String companyPage() {
        return "company";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        RedirectAttributes redirectAttributes,
                        HttpSession session,
                        Model model) {
        // 이메일로 사용자 조회
        MemberDomain memberDomain = memberService.findByEmail(email);

        // 사용자 존재 여부와 비밀번호 확인
        if (memberDomain != null && passwordEncoder.matches(password, memberDomain.getPassword())) {
            sessreion.setAttribute("loggedInUser", memberDomain);

            //사용자 역할에 따라 redirect
            Role role = memberDomain.getRole();
                if ("ADMIN".equals(role)) {
                    return "redirect:/admin";
                } else if ("COMPANY".equals(role)) {
                    return "redirect:/company";
                } else return "redirect:/";
            }

        // 로그인 실패 시, 에러 메시지를 표시하거나 로그인 페이지로 리다이렉트
        redirectAttributes.addFlashAttribute("error", "이메일 혹은 비밀번호를 확인하세요."); // 에러 메시지 추가
        return "redirect:/login"; // 로그인 실패
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}


