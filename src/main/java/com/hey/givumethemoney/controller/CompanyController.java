package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequiredArgsConstructor
public class CompanyController {

    private final MemberService memberService;

    @GetMapping("/company")
    public String companyDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        System.out.println("CompanyController called for user: " + email);

        MemberDomain member = memberService.findByEmail(email);
        model.addAttribute("userName", member.getUserName());
        model.addAttribute("companyName", member.getCompanyName());
        model.addAttribute("role", member.getRole());

        return "company";
    }

}
