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
        // 진행 중인 기부 중 상위 3개 가져오기
        List<Donation> ongoingDonations = donationService.getDonations().stream()
                .filter(donation -> donation.getEndDate().isAfter(LocalDate.now())) // 마감일이 지나지 않은 기부만 필터링
                .limit(3) // 상위 3개만 가져오기
                .collect(Collectors.toList());

        model.addAttribute("ongoingDonations", ongoingDonations);

        // 마감된 기부 중 상위 3개 가져오기
        List<Donation> finishedDonations = donationService.getDonations().stream()
                .filter(donation -> donation.getEndDate().isBefore(LocalDate.now())) // 마감일이 지난 기부만 필터링
                .limit(3) // 상위 3개만 가져오기
                .collect(Collectors.toList());

        model.addAttribute("finishedDonations", finishedDonations);

        return "index";
    }
}
