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
    public DonationController(ImageService imageService, 
                                DonationService donationService, 
                                ProductService productService, 
                                MemberService memberService, 
                                CustomUserService customUserService) {
        this.donationService = donationService;
        this.imageService = imageService;
        this.productService = productService;
        this.memberService = memberService;
        this.customUserService = customUserService;
    }

    @GetMapping("/detail/{id}")
    //@ResponseBody
    public String detail(@PathVariable("id") Long id, Model model) {
        Optional<Donation> donation = donationService.getDonationById(id);
        Optional<WaitingDonation> notConfirmed = donationService.getWaitingDonationById(id);

        List<Image> images = List.of();

        // 유저 정보 전달
        String email = customUserService.getEmail();
        if (email.equals("anonymous")) {
            model.addAttribute("user", null);
        }
        else {
            MemberDomain member = memberService.findByEmail(email);
            model.addAttribute("user", member);
        }

        if (donation.isPresent() && notConfirmed.isEmpty()) {
            if (getUserType() != Role.ADMIN && !email.equals(donation.get().getUserId()) && donation.get().getStartDate().isAfter(LocalDate.now())) {
                model.addAttribute("msg", "준비 중인 기부입니다.");
                model.addAttribute("url", "/");

                return "alert";
            }
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
            // 다른 사용자가 작성한 건 볼 수 없음
            if (getUserType() != Role.ADMIN && !email.equals(notConfirmed.get().getUserId())) {
                model.addAttribute("msg", "준비 중인 기부입니다.");
                model.addAttribute("url", "/");

                return "alert";
            }
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
    public String confirm(@PathVariable("id") Long id, Model model) {

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
    public String reject(@PathVariable("id") Long id, @RequestParam(value = "rejectionReason") String rejectionReason, Model model) {
        
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
        // 유저 서비스에서 현재 로그인된 유저 id 받아옴
        String currentUserId = customUserService.getEmail();
        if (currentUserId.equals("anonymous")) {
            model.addAttribute("user", null);
        }
        else {
            MemberDomain member = memberService.findByEmail(currentUserId);
            model.addAttribute("user", member);
        }

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
    public String editingApplication(@RequestParam("id") Long id, Model model) {

        Optional<WaitingDonation> donation = donationService.getWaitingDonationById(id);

        // 유저 서비스에서 현재 로그인된 유저 id 받아옴
        String currentUserId = customUserService.getEmail();
        if (currentUserId.equals("anonymous")) {
            model.addAttribute("user", null);
        }
        else {
            MemberDomain member = memberService.findByEmail(currentUserId);
            model.addAttribute("user", member);
        }

        // 유저id 일치한지 확인하는 것으로 수정
        if (!customUserService.getEmail().equals(donation.get().getUserId())) {
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
            // List<ThumbNail> thumbNails = thumbNailService.findByDonationId(id);
            // model.addAttribute("thumbNails", thumbNails);

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
            @ModelAttribute DonationForm form) throws IOException {

        WaitingDonation waitingDonation = new WaitingDonation(form.getTitle(), form.getStartDate(), form.getEndDate(), form.getGoal(), 0, form.getDescript(), 0, form.getEnterName(), customUserService.getEmail());
        donationService.saveWaitingDonation(waitingDonation);

        for (MultipartFile file : files) {
            if (!file.getOriginalFilename().isEmpty() && file.getName() != null) {
                Image savedImg = imageService.saveImage(file, waitingDonation.getId());
                if (savedImg == null) {
                    System.out.println("이미지 저장 실패");
                }
                System.out.println("donationController: 이미지 저장 완료... ");
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

        StringTokenizer st = new StringTokenizer(deleteImageList);
        while (st.hasMoreTokens()) {
            Long imageId = Long.parseLong(st.nextToken().trim());
            imageService.deleteImageById(imageId);
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
            if (!file.getOriginalFilename().isEmpty() && file.getName() != null) {
                Image savedImg = imageService.saveImage(file, waitingDonation.get().getId());
                if (savedImg == null) {
                    System.out.println("이미지 저장 실패");
                }
                System.out.println("donationController: 이미지 저장 완료... ");
            }
        }
        
        return "redirect:/detail/" + id.toString();
    }

    @GetMapping("/waitingList")
    public String redirectWaitingList() {
        return "redirect:/waitingList/1";
    }

    @GetMapping("/waitingList/{page}")
    public String showWaitingList(@PathVariable("page") int page, Model model) {
        Role userType = getUserType();

        List<WaitingDonation> waitingDonations = new ArrayList<>();

        // 유저 서비스에서 현재 로그인된 유저 id 받아옴
        String currentUserId = customUserService.getEmail();
        if (currentUserId.equals("anonymous")) {
            model.addAttribute("user", null);
        }
        else {
            MemberDomain member = memberService.findByEmail(currentUserId);
            model.addAttribute("user", member);
        }
        

        if (userType == Role.ADMIN) {
            waitingDonations = donationService.getWaitingDonations();
        }
        else if (userType == Role.COMPANY) {
            // 해당 기업이 작성한 목록으로 받아오기
            waitingDonations = donationService.getWaitingDonationsByUserId(currentUserId);
        }
        else {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");

            return "alert";
        }

        List<WaitingDonation> waitingDonationsByPage = new ArrayList<>();
        int dCnt = 0;
        for (int i = 0; i < waitingDonations.size(); i++) {
            if (dCnt >= (page - 1) * LIST_COUNT + LIST_COUNT) {
                break;
            }
            // 마감되지 않은 기부만 추출
            if (waitingDonations.get(i).getEndDate().compareTo(LocalDate.now()) >= 0) {
                if (dCnt >= (page - 1) * LIST_COUNT) {
                    waitingDonationsByPage.add(waitingDonations.get(i));
                }
                dCnt++;
            }
        }

        model.addAttribute("isEnded", false);
        model.addAttribute("donations", waitingDonationsByPage);
        model.addAttribute("pageCnt", waitingDonations.size() / LIST_COUNT);
        model.addAttribute("listCnt", LIST_COUNT);
        model.addAttribute("listType", "waiting");

        return "applicationList";
    }

    @GetMapping("/applicationList")
    public String redirectList() {
        return "redirect:/applicationList/1";
    }

    @GetMapping("/applicationList/{page}")
    public String showApplicationList(@PathVariable("page") int page, Model model) {
        // 홈에서 기부자에게 보여질 목록과 다른 목록임

        Role userType = getUserType();

        // 유저 서비스에서 현재 로그인된 유저 id 받아옴
        String currentUserId = customUserService.getEmail();
        if (currentUserId.equals("anonymous")) {
            model.addAttribute("user", null);
        }
        else {
            MemberDomain member = memberService.findByEmail(currentUserId);
            model.addAttribute("user", member);
        }

        List<DonationBase> result = new ArrayList<>();;

        if (userType == Role.ADMIN) {
            result.addAll(donationService.getWaitingDonations());
            result.addAll(donationService.getDonations());
        }
        else if (userType == Role.COMPANY) {

            // 해당 기업이 작성한 목록으로 받아오기
            result.addAll(donationService.getWaitingDonationsByUserId(currentUserId));
            result.addAll(donationService.getDonationsByUserId(currentUserId));
        }
        else {
            model.addAttribute("msg", "로그인이 필요합니다.");
            model.addAttribute("url", "/login");

            return "alert";
        }

        List<DonationBase> donationsByPage = new ArrayList<>();
        int dCnt = 0;
        for (int i = 0; i < result.size(); i++) {
            if (dCnt >= (page - 1) * LIST_COUNT + LIST_COUNT) {
                break;
            }
            // 마감되지 않은 기부만 추출
            if (result.get(i).getEndDate().compareTo(LocalDate.now()) >= 0) {
                if (dCnt >= (page - 1) * LIST_COUNT) {
                    donationsByPage.add(result.get(i));
                }
                dCnt++;
            }
        }

        model.addAttribute("donations", donationsByPage);
        model.addAttribute("isEnded", false);
        model.addAttribute("pageCnt", result.size() / LIST_COUNT);
        model.addAttribute("listCnt", LIST_COUNT);
        model.addAttribute("listType", "application");
        
        return "applicationList";
    }


    @GetMapping("/endList")
    public String redirectEndList() {
        return "redirect:/endList/1";
    }

    // 마감된 기부 보기
    @GetMapping("/endList/{page}")
    public String showEndedApplicationList(@PathVariable("page") int page, Model model) {

        List<Donation> donations;
        // 유저 서비스에서 현재 로그인된 유저 id 받아옴
        String currentUserId = customUserService.getEmail();
        if (currentUserId.equals("anonymous")) {
            model.addAttribute("user", null);
        }
        else {
            MemberDomain member = memberService.findByEmail(currentUserId);
            model.addAttribute("user", member);
        }

        // 보여질 리스트
        List<Donation> donationsByPage = new ArrayList<>();

        if (getUserType() == Role.COMPANY) {

            donations = donationService.getDonationsByUserId(currentUserId);
        }
        else {
            // 전체 마감 목록
            donations = donationService.getDonations();
        }

        for (int i = (page - 1) * LIST_COUNT; i < (page - 1) * LIST_COUNT + LIST_COUNT;) {
            if (i >= donations.size()) {
                break;
            }
            if (donations.get(i).getEndDate().isBefore(LocalDate.now())) {
                donationsByPage.add(donations.get(i));
                i++;
            }
        }

        model.addAttribute("isCompany", getUserType() == Role.COMPANY);
        model.addAttribute("isEnded", true);
        model.addAttribute("donations", donationsByPage);
        model.addAttribute("pageCnt", donations.size() / LIST_COUNT);
        model.addAttribute("listCnt", LIST_COUNT);
        model.addAttribute("listType", "end");

        return "applicationList";

    }

    protected Role getUserType() {
        return customUserService.getRole();
    }

    @GetMapping("/thumbs/{id}")
    @ResponseBody
    public String showThumb(@PathVariable("id") Long id, Model model) throws IOException {
        Image image = imageService.findImageById(id).get();
        if (image != null) {
            return image.getThumbUrl();
        }
        else {
            return null;
        }
    }
}
