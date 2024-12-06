package com.hey.givumethemoney.controller;

import org.springframework.stereotype.Controller;

import com.hey.givumethemoney.service.NaverOCRService;

@Controller
public class NaverOCRController {
    
    private final NaverOCRService naverOCRService;

    // 생성자 주입
    public NaverOCRController(NaverOCRService naverOCRService) {
        this.naverOCRService = naverOCRService;
    }
}
