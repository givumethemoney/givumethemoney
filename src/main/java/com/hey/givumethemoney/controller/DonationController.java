package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.WaitingDonation;
import com.hey.givumethemoney.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class DonationController {

    final int LIST_COUNT = 10;

    DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping("/detail/{id}")
    //@ResponseBody
    public String detail(@PathVariable Long id, Model model) {
        Optional<Donation> donation = donationService.getDonationById(id);
        Optional<WaitingDonation> notConfirmed = donationService.getWaitingDonationById(id);

        if (donation.isPresent() && !notConfirmed.isPresent()) {
            model.addAttribute("donation", donation.get());
        }
        else if (notConfirmed.isPresent()) {
            model.addAttribute("donation", notConfirmed.get());
        }


        return "donationDetail";
    }

    @GetMapping("/application/confirm/{id}")
    public String confirm(@PathVariable Long id, Model model) {
        Optional<WaitingDonation> donation = donationService.getWaitingDonationById(id);

        if (donation.isPresent()) {
            Donation confirmed = new Donation((WaitingDonation)donation.get());
            donationService.saveDonation(confirmed);
            donationService.deleteWaitingDonationById(id);
        }

        return "redirect:/detail/{id}";
    }

    @GetMapping("/application")
    public String application() {
        return "donationForm";
    }

    @PostMapping("/application/submit")
    public String submitApplication(DonationForm form) {
        WaitingDonation waitingDonation = new WaitingDonation(form.getTitle(), form.getStartDate(), form.getEndDate(), form.getGoal(), 0, form.getDescript(), 0, form.getEnterName(), null, false, "test");
        donationService.saveWaitingDonation(waitingDonation);
        return "redirect:/application";
    }

    @GetMapping("/admin/applicationList/{page}")
    public String confirmApplicationList(@PathVariable int page, Model model) {
        List<WaitingDonation> waitingDonations = donationService.getWaitingDonations();

        List<WaitingDonation> donationsToConfirm = new ArrayList<>();
        for (int i = (page - 1) * LIST_COUNT; i < (page - 1) * LIST_COUNT + LIST_COUNT; i++ ) {
            if (i >= waitingDonations.size()) {
                break;
            }
            donationsToConfirm.add(waitingDonations.get(i));
        }

        model.addAttribute("donationsToConfirm", donationsToConfirm);

        return "applicationList";
    }
}
