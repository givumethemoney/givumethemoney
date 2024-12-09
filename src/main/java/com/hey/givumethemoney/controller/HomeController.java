package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.service.DonationService;
import com.hey.givumethemoney.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final DonationService donationService;
    private final ImageService imageService;

    @Autowired
    public HomeController(DonationService donationService, ImageService imageService) {
        this.donationService = donationService;
        this.imageService=imageService;
    }

    @GetMapping("/")
    public String index(Model model) {
        // 진행 중인 기부 중 상위 3개 가져오기
        List<Donation> ongoingDonations = donationService.getDonations().stream()
                .filter(donation -> donation.getEndDate().isAfter(LocalDate.now())) // 마감일이 지나지 않은 기부만 필터링
                .limit(3) // 상위 3개만 가져오기
                .collect(Collectors.toList());

        // 마감된 기부 중 상위 3개 가져오기
        List<Donation> finishedDonations = donationService.getDonations().stream()
                .filter(donation -> donation.getEndDate().isBefore(LocalDate.now())) // 마감일이 지난 기부만 필터링
                .limit(3) // 상위 3개만 가져오기
                .collect(Collectors.toList());


        // 썸네일 매핑
        Map<Long, String> thumbnailMap = new HashMap<>();

        // 진행 중 기부에 대해 썸네일(첫 번째 이미지) 추가
        for (Donation donation : ongoingDonations) {
            List<Image> images = imageService.findImagesByDonationId(donation.getId());
            if (!images.isEmpty()) {
                // 첫 번째 이미지를 썸네일로 설정
                thumbnailMap.put(donation.getId(), images.get(0).getThumbUrl());
            }
        }

        // 마감된 기부에 대해 썸네일(첫 번째 이미지) 추가
        for (Donation donation : finishedDonations) {
            List<Image> images = imageService.findImagesByDonationId(donation.getId());
            if (!images.isEmpty()) {
                // 첫 번째 이미지를 썸네일로 설정
                thumbnailMap.put(donation.getId(), images.get(0).getThumbUrl());
            }
        }


        model.addAttribute("ongoingDonations", ongoingDonations);
        model.addAttribute("finishedDonations", finishedDonations);
        model.addAttribute("thumbnails", thumbnailMap);

        return "index";
    }
}
