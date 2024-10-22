package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.Image;
import com.hey.givumethemoney.domain.Product;
import com.hey.givumethemoney.domain.WaitingDonation;
import com.hey.givumethemoney.service.DonationService;
import com.hey.givumethemoney.service.ImageService;
import com.hey.givumethemoney.service.ProductService;
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
import java.util.Random;

@Controller
public class DonationController {

    final int LIST_COUNT = 10;

    DonationService donationService;
    ImageService imageService;
    ProductService productService;

    @Autowired
    public DonationController(ImageService imageService, DonationService donationService, ProductService productService) {
        this.donationService = donationService;
        this.imageService = imageService;
        this.productService = productService;
    }

    @GetMapping("/detail/{id}")
    //@ResponseBody
    public String detail(@PathVariable Long id, Model model) {
        Optional<Donation> donation = donationService.getDonationById(id);
        Optional<WaitingDonation> notConfirmed = donationService.getWaitingDonationById(id);

        List<Image> images = List.of();
        Long donationId = 0L;

        if (donation.isPresent() && notConfirmed.isEmpty()) {
            model.addAttribute("donation", donation.get());
            images = imageService.findImagesByDonationId(donation.get().getId());
            donationId = donation.get().getId();
        }
        else if (notConfirmed.isPresent()) {
            model.addAttribute("donation", notConfirmed.get());
            images = imageService.findImagesByDonationId(notConfirmed.get().getId());
            donationId = notConfirmed.get().getId();
        }
        model.addAttribute("images", images);

        List<Product> productList = productService.getProductsByDonationId(donationId);
        model.addAttribute("productList", productList);

        // 현재 기부 물품 개수
        if (!productList.isEmpty()) {
            Random random = new Random();
            int randInt = random.nextInt(productList.size());

            model.addAttribute("random", random.nextInt(productList.size()));
        }

        return "donationDetail";
    }

    @GetMapping("/application/confirm/{id}")
    public String confirm(@PathVariable Long id, Model model) {
        Optional<WaitingDonation> donation = donationService.getWaitingDonationById(id);

        if (donation.isPresent()) {
            donation.get().setRejected(false);
            donation.get().setRejectionReason("");
            Donation confirmed = new Donation((WaitingDonation)donation.get());
            donationService.saveDonation(confirmed);
            donationService.deleteWaitingDonationById(id);
        }

        return "redirect:/detail/{id}";
    }

    @PostMapping("/application/reject/{id}")
    public String reject(@PathVariable Long id, @RequestParam(value = "rejectionReason") String rejectionReason, Model model) {
        Optional<WaitingDonation> donation = donationService.getWaitingDonationById(id);

        if (donation.isPresent()) {
            donation.get().setRejected(true);
            donation.get().setRejectionReason(rejectionReason);
            donationService.saveWaitingDonation(donation.get());
        }

        return "redirect:/detail/{id}";
    }

    @GetMapping("/application/agree")
    public String agree() {
        return "agree";
    }

    @GetMapping("/application/write")
    public String application(@RequestParam(value="chk-all", required = false) boolean checkAll, Model model) {
        if (checkAll) {
            return "donationForm";
        }
        else {
            // 동의 없이 접근하면 오류 띄우기
            model.addAttribute("msg", "약관 동의가 필요합니다.");
            model.addAttribute("url", "/application/agree");

            return "alert";
        }
    }

    @PostMapping("/application/submit")
    public String submitApplication(@RequestParam(value = "images", required = false) List<MultipartFile> files, @RequestParam(value = "productName") List<String> productName, @RequestParam(value = "productPrice") List<Long> productPrice, DonationForm form) throws IOException {
        WaitingDonation waitingDonation = new WaitingDonation(form.getTitle(), form.getStartDate(), form.getEndDate(), form.getGoal(), 0, form.getDescript(), 0, form.getEnterName(), "test");
        donationService.saveWaitingDonation(waitingDonation);

        for (MultipartFile file : files) {
            if (imageService.saveImages(file, waitingDonation.getId()) == null) {
                // 이미지 확장자 외 파일 오류
                System.out.println("다른 확장자");
            }
        }

        List<Product> productList = new ArrayList<>();
        if (!productName.isEmpty() && !productPrice.isEmpty()) {
            if (productName.size() != productPrice.size()) {
                // 오류 메시지
            }
            else {
                for (int i = 0; i < productName.size(); i++) {
                    Product p = new Product(productName.get(i), productPrice.get(i), waitingDonation.getId());
                    productList.add(p);
                }

                productService.saveProducts(productList);
            }
        }

        return "redirect:/application";
    }

    // user 테이블 생성되면 userType 받아서 applicationList 두 개 합치기
    @GetMapping("/admin/applicationList/{page}")
    public String confirmApplicationList(@PathVariable int page, Model model) {
        List<WaitingDonation> waitingDonations = donationService.getWaitingDonations();

        List<WaitingDonation> waitingDonationsByPage = new ArrayList<>();
        for (int i = (page - 1) * LIST_COUNT; i < (page - 1) * LIST_COUNT + LIST_COUNT; i++ ) {
            if (i >= waitingDonations.size()) {
                break;
            }
            waitingDonationsByPage.add(waitingDonations.get(i));
        }

        model.addAttribute("donationsToConfirm", waitingDonationsByPage);

        return "applicationList";
    }

    @GetMapping("/enterprise/applicationList/{page}")
    public String showApplicationList(@PathVariable int page, Model model) {
        // 유저 서비스에서 현재 로그인된 유저 id 받아옴
        String currentUserId = "Test";

        List<WaitingDonation> waitingDonations = donationService.getWaitingDonationsByUserId(currentUserId);
        
        // 보여질 리스트
        List<WaitingDonation> waitingDonationsByPage = new ArrayList<>();
        for (int i = (page - 1) * LIST_COUNT; i < (page - 1) * LIST_COUNT + LIST_COUNT; i++ ) {
            if (i >= waitingDonations.size()) {
                break;
            }
            waitingDonationsByPage.add(waitingDonations.get(i));
        }

        model.addAttribute("donationsToConfirm", waitingDonationsByPage);
        
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
