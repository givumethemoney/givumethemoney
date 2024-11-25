package com.hey.givumethemoney.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class CompanyController {
    @GetMapping("/company")
    public String companyP() {
        return "company Controller";
    }
}
