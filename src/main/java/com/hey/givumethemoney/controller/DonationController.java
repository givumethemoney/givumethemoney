package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.domain.WaitingDonation;
import com.hey.givumethemoney.service.DonationService;
import com.hey.givumethemoney.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class DonationController {

    final int LIST_COUNT = 10;

    DonationService donationService;
    ImageService imageService;

    @Autowired
    public DonationController(ImageService imageService, DonationService donationService) {
        this.donationService = donationService;
        this.imageService = imageService;
    }

    @GetMapping("/detail/{id}")
    //@ResponseBody
    public String detail(@PathVariable Long id, Model model) {
        Optional<Donation> donation = donationService.getDonationById(id);
        Optional<WaitingDonation> notConfirmed = donationService.getWaitingDonationById(id);

        List<Image> images = List.of();

        if (donation.isPresent() && !notConfirmed.isPresent()) {
            model.addAttribute("donation", donation.get());
            images = imageService.findImagesByDonationId(donation.get().getId());
        }
        else if (notConfirmed.isPresent()) {
            model.addAttribute("donation", notConfirmed.get());
            images = imageService.findImagesByDonationId(notConfirmed.get().getId());
        }
        model.addAttribute("images", images);

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
    public String submitApplication(@RequestParam(value = "images", required = false) List<MultipartFile> files, DonationForm form) throws IOException {
        WaitingDonation waitingDonation = new WaitingDonation(form.getTitle(), form.getStartDate(), form.getEndDate(), form.getGoal(), 0, form.getDescript(), 0, form.getEnterName(), false, "test");
        donationService.saveWaitingDonation(waitingDonation);

        for (MultipartFile file : files) {
            if (imageService.saveImages(file, waitingDonation.getId()) == null) {
                // 이미지 확장자 외 파일 오류
                System.out.println("다른 확장자");
            }

        }

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

    @GetMapping("images/{id}")
    @ResponseBody
    public UrlResource showImage(@PathVariable Long id, Model model) throws IOException {
        Optional<Image> image = imageService.findImageById(id);
        if (image.isPresent()) {
            return new UrlResource("file:" + image.get().getSavedPath());

        }
        else {
            return null;
        }
    }
}
