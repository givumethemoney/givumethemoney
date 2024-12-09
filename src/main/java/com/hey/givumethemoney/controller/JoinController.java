package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.dto.MemberDTO;
import com.hey.givumethemoney.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.mapping.Join;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JoinController {

    private final MemberService memberService;

    @Autowired
    public JoinController(MemberService memberService) {
        this.memberService = memberService;
    }

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