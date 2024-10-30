package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.*;
import com.hey.givumethemoney.service.DonationService;
import com.hey.givumethemoney.service.ImageService;
import com.hey.givumethemoney.service.ProductService;
import com.hey.givumethemoney.service.ReceiptService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

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

            if (donation.get().getEndDate().isBefore(LocalDate.now())) {
                model.addAttribute("isEnded", true);
            }
            else {
                model.addAttribute("isEnded", false);
            }
        }
        else if (notConfirmed.isPresent()) {
            model.addAttribute("donation", notConfirmed.get());
            images = imageService.findImagesByDonationId(notConfirmed.get().getId());
            donationId = notConfirmed.get().getId();

            model.addAttribute("isEnded", false);

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
            model.addAttribute("isEdit", false);
            return "donationForm";
        }
        else {
            // 동의 없이 접근하면 오류 띄우기
            model.addAttribute("msg", "약관 동의가 필요합니다.");
            model.addAttribute("url", "/application/agree");

            return "alert";
        }
    }

    @GetMapping("/application/edit")
    public String editingApplication(@RequestParam(value="id") Long id, Model model) {
        Optional<WaitingDonation> donation = donationService.getWaitingDonationById(id);

        if (donation.isPresent()) {
            model.addAttribute("donation", donation.get());
            model.addAttribute("isEdit", true);

            List<Image> images = imageService.findImagesByDonationId(id);
            model.addAttribute("images", images);
            List<Product> productList = productService.getProductsByDonationId(id);
            model.addAttribute("productList", productList);

            return "donationForm";
        }
        else {
            model.addAttribute("msg", "일치하는 기부 신청서가 없습니다.");
            model.addAttribute("url", "/admin/applicationList");

            return "alert";
        }
    }

    @PostMapping("/application/submit")
    public String submitApplication(
            @RequestParam(value = "images", required = false) List<MultipartFile> files,
            @RequestParam(value = "productName") List<String> productName,
            @RequestParam(value = "productPrice") List<Long> productPrice,
            DonationForm form) throws IOException {
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

        return "redirect:/detail/" + waitingDonation.getId().toString();
    }

    @PostMapping("/application/submitEdit")
    public String editApplication(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "images", required = false) List<MultipartFile> files,
            @RequestParam(value = "productName") List<String> productName,
            @RequestParam(value = "productPrice") List<Long> productPrice,
            @RequestParam(value = "deleteImages") String deleteImageList,
            DonationForm form) throws IOException {
        // 전부 지운 후 새로 들어온 리스트로 저장
        productService.deleteProductsByDonationId(id);

        StringTokenizer st = new StringTokenizer(deleteImageList);
        while (st.hasMoreTokens()) {
            Long imageId = Long.parseLong(st.nextToken().trim());
            imageService.deleteImageById(imageId);
            // 실제 저장된 이미지도 지워야 함
        }

        Optional<WaitingDonation> waitingDonation = donationService.getWaitingDonationById(id);

        if (waitingDonation.isPresent()) {
            waitingDonation.get().setTitle(form.getTitle());
            waitingDonation.get().setStartDate(form.getStartDate());
            waitingDonation.get().setEndDate(form.getEndDate());
            waitingDonation.get().setGoal(form.getGoal());
            waitingDonation.get().setEnterName(form.getEnterName());
            waitingDonation.get().setDescription(form.getDescript());
            waitingDonation.get().setRejected(false);
            waitingDonation.get().setRejectionReason("");
            donationService.saveWaitingDonation(waitingDonation.get());
        }

        for (MultipartFile file : files) {
            if (imageService.saveImages(file, id) == null) {
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
                    Product p = new Product(productName.get(i), productPrice.get(i), id);
                    productList.add(p);
                }

                productService.saveProducts(productList);
            }
        }
        
        return "redirect:/detail/" + id.toString();
    }

    @GetMapping("/admin/applicationList")
    public String redirectList() {
        return "redirect:/admin/applicationList/1";
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

        model.addAttribute("isEnded", false);
        model.addAttribute("donations", waitingDonationsByPage);

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

        model.addAttribute("donations", waitingDonationsByPage);

        model.addAttribute("isEnded", false);
        return "applicationList";
    }

    @GetMapping("/applicationList/end")
    public String redirectEndList() {
        return "redirect:/applicationList/end/1";
    }

    // 마감된 기부 보기
    @GetMapping("/applicationList/end/{page}")
    public String showEndedApplicationList(@PathVariable int page, Model model) {
        // 기업 사용자인지 확인
        // 일반 유저면 로그인 or 오류 페이지 띄우기

        // 유저 서비스에서 현재 로그인된 유저 id 받아옴
        String currentUserId = "Test";

        //List<Donation> donations = donationService.getDonationsByUserId(currentUserId);
        // 테스트용
        List<Donation> donations = donationService.getDonations();

        // 보여질 리스트
        List<Donation> donationsByPage = new ArrayList<>();
        for (int i = (page - 1) * LIST_COUNT; i < (page - 1) * LIST_COUNT + LIST_COUNT; i++ ) {
            if (i >= donations.size()) {
                break;
            }
            if (donations.get(i).getEndDate().isBefore(LocalDate.now())) {
                donationsByPage.add(donations.get(i));
            }
        }
        model.addAttribute("isEnded", true);
        model.addAttribute("donations", donationsByPage);

        return "applicationList";
    }


    @GetMapping("/donation/complete")
    public String donationComplete(
            @RequestParam(value="id") Long id,
            @RequestParam(value="value") int value) throws IOException {
        Optional<Donation> donation = donationService.getDonationById(id);

        if (donation.isPresent()) {
            donation.get().setCurrentAmount(donation.get().getCurrentAmount() + value);
            donation.get().setParticipant(donation.get().getParticipant() + 1);
        }

        // 기부 완료 페이지로 넘어감
        return "redirect:/complete";
    }
}
