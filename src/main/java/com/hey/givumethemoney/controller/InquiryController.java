package com.hey.givumethemoney.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hey.givumethemoney.domain.*;
import com.hey.givumethemoney.service.*;

@Controller
public class InquiryController {

    private final InquiryService inquiryService;

    // 생성자 주입
    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    // 문의 저장
    @PostMapping("/sendMessage")
    public String saveInquiry(
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "message", required = false) String message,
        @RequestParam(name = "redirectUrl") String redirectUrl
    ) {
        Inquiry inquiry = new Inquiry();
        inquiry.setName(name);
        inquiry.setEmail(email);
        inquiry.setMessage(message);
        inquiryService.saveInquiry(inquiry);
        System.out.println(inquiry);

        return "redirect:" + redirectUrl;
    }

}
