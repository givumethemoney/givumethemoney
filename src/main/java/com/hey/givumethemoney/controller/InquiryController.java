package com.hey.givumethemoney.controller;

import org.codehaus.groovy.runtime.dgmimpl.arrays.IntegerArrayGetAtMetaMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;


import com.hey.givumethemoney.controller.*;
import com.hey.givumethemoney.domain.*;
import com.hey.givumethemoney.service.*;

@Controller
public class InquiryController {

    InquiryService inquiryService;

    // 생성자 주입
    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    // 문의 저장
    @PostMapping
    @RequestMapping("/sendMessage")
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
