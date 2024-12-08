package com.hey.givumethemoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.hey.givumethemoney.service.NaverOCRService;

@Controller
public class NaverOCRController {
    
    private final NaverOCRService naverOCRService;

    // 생성자 주입
    @Autowired
    public NaverOCRController(NaverOCRService naverOCRService) {
        this.naverOCRService = naverOCRService;
    }
}
