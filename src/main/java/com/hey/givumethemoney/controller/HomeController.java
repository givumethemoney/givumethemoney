package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final DonationService donationService;

    @Autowired
    public HomeController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Donation> donationList = donationService.getTop3Donations(); // 진행 중인 상위 3개의 기부를 가져옴
        model.addAttribute("donationList", donationList);

        return "index";
    }
}
