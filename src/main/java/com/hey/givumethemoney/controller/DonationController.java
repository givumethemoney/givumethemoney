package com.hey.givumethemoney.controller;

import com.hey.givumethemoney.domain.*;
import com.hey.givumethemoney.service.*;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;

@Controller
public class DonationController {

    final int LIST_COUNT = 10;

    DonationService donationService;
    ImageService imageService;
    ProductService productService;
    MemberService memberService;
    CustomUserService customUserService;

    // 컨트롤러 클래스 내에 로거 추가
    private static final Logger logger = LoggerFactory.getLogger(DonationController.class);

    @Autowired
    public DonationController(ImageService imageService, DonationService donationService, ProductService productService, MemberService memberService, CustomUserService customUserService) {
        this.donationService = donationService;
        this.imageService = imageService;
        this.productService = productService;
        this.memberService = memberService;
        this.customUserService = customUserService;
    }

    @GetMapping("/detail/{id}")
    //@ResponseBody
    public String detail(@PathVariable Long id, Model model) {
        Optional<Donation> donation = donationService.getDonationById(id);
        Optional<WaitingDonation> notConfirmed = donationService.getWaitingDonationById(id);

        List<Image> images = List.of();

        // 유저 정보 전달
        String email = customUserService.getEmail();
        MemberDomain member = memberService.findByEmail(email);
        model.addAttribute("user", member);

        if (donation.isPresent() && notConfirmed.isEmpty()) {
            model.addAttribute("donation", donation.get());

            if (donation.get().getEndDate().isBefore(LocalDate.now())) {
                model.addAttribute("isEnded", true);
            }
            else {
                model.addAttribute("isEnded", false);
            }
             // 상위 30개 닉네임 가져옴
            List<Map.Entry<String, Integer>> topNicknames = donationService.getTopNicknameDonations(id);
            // 상위 3개만 가져오기
            List<Map.Entry<String, Integer>> top3Nicknames = topNicknames.stream()
                .limit(3)
                .collect(Collectors.toList());

            // topNicknames가 null이 아니고 비어 있지 않을 경우에만 모델에 추가
            if (topNicknames != null && !topNicknames.isEmpty()) {
                model.addAttribute("topNicknames", topNicknames);
            }
            if (top3Nicknames != null && !top3Nicknames.isEmpty()) {
                model.addAttribute("top3Nicknames", top3Nicknames);
            }
        }
        // 아직 승인되지 않은 기부인 경우
        else if (notConfirmed.isPresent() && donation.isEmpty()) {
            model.addAttribute("donation", notConfirmed.get());

            model.addAttribute("isEnded", false);

        }

        images = imageService.findImagesByDonationId(id);
        model.addAttribute("images", images);

        List<Product> productList = productService.getProductsByDonationId(id);
        model.addAttribute("productList", productList);

        // 현재 기부 물품 개수
        if (!productList.isEmpty()) {
            Random random = new Random();
            int randInt = random.nextInt(productList.size());

            model.addAttribute("random", randInt);
        } else {
            model.addAttribute("random", -1); // -1은 유효하지 않은 인덱스를 의미
        }

        // 모델에 들어있는 데이터 확인
        // logger.info("Model contains: {}", model.asMap());

        return "donationDetail";
    }

    @GetMapping("/application/confirm/{id}")
    public String confirm(@PathVariable Long id, Model model) {

        if (getUserType() != Role.ADMIN) {
            model.addAttribute("msg", "관리자가 아닙니다.");
            model.addAttribute("url", "/");

            return "alert";
        }

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
        
        if (getUserType() != Role.ADMIN) {
            model.addAttribute("msg", "관리자가 아닙니다.");
            model.addAttribute("url", "/");

            return "alert";
        }

        Optional<WaitingDonation> donation = donationService.getWaitingDonationById(id);

        if (donation.isPresent()) {
            donation.get().setRejected(true);
            donation.get().setRejectionReason(rejectionReason);
            donationService.saveWaitingDonation(donation.get());
        }

        return "redirect:/detail/{id}";
    }

    @GetMapping("/application/agree")
    public String agree(Model model) {

        if (getUserType() != Role.ADMIN && getUserType() != Role.COMPANY) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");

            return "alert";
        }

        return "agree";
    }

    @GetMapping("/application/write")
    public String application(@RequestParam(value="chk-all", required = false) boolean checkAll, Model model) {

        if (getUserType() != Role.ADMIN && getUserType() != Role.COMPANY) {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");

            return "alert";
        }

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

        // 유저id 일치한지 확인하는 것으로 수정
        if (customUserService.getEmail() != donation.get().getUserId()) {
            model.addAttribute("msg", "해당 기업이 아닙니다.");
            model.addAttribute("url", "/");

            return "alert";
        }

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
        // 유저 id 수정
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

//    // user 테이블 생성되면 userType 받아서 applicationList 두 개 합치기
//    @GetMapping("/admin/applicationList/{page}")
//    public String confirmApplicationList(@PathVariable int page, Model model) {
//        List<WaitingDonation> waitingDonations = donationService.getWaitingDonations();
//
//        List<WaitingDonation> waitingDonationsByPage = new ArrayList<>();
//        for (int i = (page - 1) * LIST_COUNT; i < (page - 1) * LIST_COUNT + LIST_COUNT; i++ ) {
//            if (i >= waitingDonations.size()) {
//                break;
//            }
//            waitingDonationsByPage.add(waitingDonations.get(i));
//        }
//
//        model.addAttribute("isEnded", false);
//        model.addAttribute("donations", waitingDonationsByPage);
//
//        return "applicationList";
//    }
//
//    @GetMapping("/enterprise/applicationList/{page}")
//    public String showApplicationList(@PathVariable int page, Model model) {
//        // 유저 서비스에서 현재 로그인된 유저 id 받아옴
//        String currentUserId = "Test";
//
//        List<WaitingDonation> waitingDonations = donationService.getWaitingDonationsByUserId(currentUserId);
//
//        // 보여질 리스트
//        List<WaitingDonation> waitingDonationsByPage = new ArrayList<>();
//        for (int i = (page - 1) * LIST_COUNT; i < (page - 1) * LIST_COUNT + LIST_COUNT; i++ ) {
//            if (i >= waitingDonations.size()) {
//                break;
//            }
//            waitingDonationsByPage.add(waitingDonations.get(i));
//        }
//
//        model.addAttribute("donations", waitingDonationsByPage);
//
//        model.addAttribute("isEnded", false);
//        return "applicationList";
//    }

    @GetMapping("/applicationList/{page}")
    public String showApplicationList(@PathVariable int page, Model model) {
        // 홈에서 기부자에게 보여질 목록과 다른 목록임

        Role userType = getUserType();

        if (userType == Role.ADMIN) {
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
        else if (userType == Role.COMPANY) {
            // 유저 서비스에서 현재 로그인된 유저 id 받아옴
            String currentUserId = "Test";

            // 해당 기업이 작성한 목록으로 받아오기
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
        else {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");

            return "alert";
        }
    }

    @GetMapping("/applicationList/end")
    public String redirectEndList() {
        return "redirect:/applicationList/end/1";
    }

    // 마감된 기부 보기
    @GetMapping("/applicationList/end/{page}")
    public String showEndedApplicationList(@PathVariable int page, Model model) {

        List<Donation> donations;

        // 보여질 리스트
        List<Donation> donationsByPage = new ArrayList<>();

        if (getUserType() == Role.COMPANY) {
            // 유저 서비스에서 현재 로그인된 유저 id 받아옴
            String currentUserId = "Test";

            donations = donationService.getDonationsByUserId(currentUserId);

            for (int i = (page - 1) * LIST_COUNT; i < (page - 1) * LIST_COUNT + LIST_COUNT; i++ ) {
                if (i >= donations.size()) {
                    break;
                }
                if (donations.get(i).getEndDate().isBefore(LocalDate.now())) {
                    donationsByPage.add(donations.get(i));
                }
            }
        }
        else {
            // 전체 마감 목록
            donations = donationService.getDonations();

            for (int i = (page - 1) * LIST_COUNT; i < (page - 1) * LIST_COUNT + LIST_COUNT; i++ ) {
                if (i >= donations.size()) {
                    break;
                }
                if (donations.get(i).getEndDate().isBefore(LocalDate.now())) {
                    donationsByPage.add(donations.get(i));
                }
            }
        }

        model.addAttribute("isEnded", true);
        model.addAttribute("donations", donationsByPage);

        return "applicationList";

    }

    protected Role getUserType() {
        return customUserService.getRole();
    }
}
