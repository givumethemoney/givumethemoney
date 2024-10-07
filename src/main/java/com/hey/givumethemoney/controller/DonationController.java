package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.service.DonationApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class DonationController {

    DonationApplicationService donationApplicationService;

    @Autowired
    public DonationController(DonationApplicationService donationApplicationService) {
        this.donationApplicationService = donationApplicationService;
    }

    @GetMapping("/application")
    public String application() {
        return "donationForm";
    }

    @PostMapping("/application/submit")
    public String submitApplication(DonationForm form) {
        System.out.println(form.getTitle());

        Donation donation = new Donation(form.getTitle(), form.getStartDate(), form.getEndDate(), form.getGoal(), 0, form.getDescript(), 0, form.getEnterName(), null, false, "test");
        donationApplicationService.saveDonation(donation);
        return "redirect:/application";
    }
}
