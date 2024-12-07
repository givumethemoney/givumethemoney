package com.hey.givumethemoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.service.CustomUserService;
import com.hey.givumethemoney.service.MemberService;

@Controller
public class HomeController {

    private final CustomUserService customUserService;
    private final MemberService memberService;

    @Autowired
    public HomeController (CustomUserService customUserService, MemberService memberService) {
        this.customUserService = customUserService;
        this.memberService = memberService;
    }

    @GetMapping("/")
    public String index(Model model){
        // 유저 정보 전달
        String email = customUserService.getEmail();
        if (email.equals("anonymous")) {
            model.addAttribute("user", null);
        }
        else {
            MemberDomain member = memberService.findByEmail(email);
            model.addAttribute("user", member);
        }

        return "index";
    }
    
}
