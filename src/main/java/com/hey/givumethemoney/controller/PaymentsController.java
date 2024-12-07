package com.hey.givumethemoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import com.hey.givumethemoney.domain.*;
import com.hey.givumethemoney.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class PaymentsController {

    private final PaymentsService paymentsService;
    private final DonationService donationService;
    private final NicknameDonationService nicknameDonationService;

    // 생성자 주입
    @Autowired
    public PaymentsController(PaymentsService paymentsService, DonationService donationService, NicknameDonationService nicknameDonationService) {
        this.paymentsService = paymentsService;
        this.donationService = donationService;
        this.nicknameDonationService = nicknameDonationService;
    }

    @GetMapping("/payments")
    public String payments (
        @RequestParam(name = "donationId") Long donationId,
        Model model
    ) {
        Optional<Donation> donation = donationService.getDonationById(donationId);
        
        if (donation.isPresent()) {
            if (donation.get().getEndDate().isBefore(LocalDate.now())) {
                model.addAttribute("msg", "모금 기간이 아닙니다.");
                model.addAttribute("url", "/detail/" + donationId.toString());

                return "alert";
            }
        }
        else {
            model.addAttribute("msg", "유효한 기부가 아닙니다.");
            model.addAttribute("url", "/");

            return "alert";
        }
        
        model.addAttribute("donationId", donationId);
        return "payments";
    }

    @GetMapping("/signup")
    public String signup () {
        return "signup";
    }

    @GetMapping("/paymentsList")
    // 사용자 id를 param으로 가져와서, 그 id에 해당하는 결제 내역을 돌려주기
    public String paymentsList(
        @RequestParam(name = "userId") Long userId,
        Model model
    ) {
        // 사용자 아이디로 결제 내역(결제 수단, 날짜, 금액)을 조회한다
        List<Payments> payments = paymentsService.findByUserId(userId);
        model.addAttribute("payments", payments);

        // 각 결제 내역에서 기부 ID를 추출하여 리스트에 저장
        List<Long> donationIds = new ArrayList<>();
        for (Payments payment : payments) {
            Long donationId = payment.getDonationId();
            if (donationId != null) {
                donationIds.add(donationId);
            }
        }

        // 기부 ID를 사용하여 주최 기업과 기부 주제를 가져오는 로직
        // List<Donation> donationList = new ArrayList<>();
        // for (Long donationId : donationIds) {
        //     Donation info = donationService.getDonationInfoById(donationId); // DonationService의 메서드 호출
        //     if (info != null) {
        //         donationList.add(info);
        //     }
        // }

        // 모델에 기부 정보 추가
        // model.addAttribute("donationList", donationList);

        return "paymentsList";
    }

    @GetMapping("/pay")
    public String widget(
        @RequestParam(name = "amount", required = false) Integer amount,
        @RequestParam(name = "customAmount", required = false) Integer customAmount,
        @RequestParam(name = "donationId") Long donationId,
        @RequestParam(name = "nickName", required = false) String nickName,
        Model model) {

        Integer finalAmount = 0;
        if (customAmount == null) {
            finalAmount = amount;
        } else if (amount == null) {
            finalAmount = customAmount;
        }

        Payments payment = new Payments();
        payment.setAmount(finalAmount);
        payment.setDonationId(donationId);
        model.addAttribute("payment", payment);

        if (nickName != null) {
            payment.setNickName(nickName);
            donationService.addNicknameDonation(donationId, nickName, finalAmount);
        }

        Optional<Donation> donation = donationService.getDonationById(donationId);
        if (donation.isPresent()) {
            model.addAttribute("donation", donation.get());
        }
        else {
            model.addAttribute("msg", "유효한 기부가 아닙니다.");
            model.addAttribute("url", "/");

            return "alert";
        }

        return "widget";
    }

    // 결제 정보 저장
    @PostMapping
    public Payments createPayment(@RequestBody Payments payment) {

        return paymentsService.savePayments(payment);
    }

    // 금액, 참여자 수 업데이트
    public void addToDonation(Long donationId, int amount) {
        Optional<Donation> donationOptional = donationService.getDonationById(donationId);
        if (donationOptional.isPresent()) {
            Donation donation = donationOptional.get();
            donation.setCurrentAmount(donation.getCurrentAmount() + amount); // current_amount 증가
            donation.setParticipant(donation.getParticipant() + 1); // participant 증가
            donationService.saveDonation(donation); // 변경된 금액을 저장
        } else {
            throw new RuntimeException("Donation not found with id " + donationId);
        }
    }

    @GetMapping("/success")
    public String success (
        @RequestParam(name = "paymentKey") String paymentKey, 
        @RequestParam(name = "amount") int amount, 
        @RequestParam(name = "orderId") String orderId, 
        @RequestParam(name = "paymentType", required = false) String paymentType, // paymentType은 OPTIONAL하게 처리
        @RequestParam(name = "donationId") Long donationId,
        Model model
    ) {
        if (paymentType == null) {
            paymentType = "NORMAL";
        }
        // amount 값을 로그로 출력해서 제대로 들어오는지 확인
        System.out.println("Received amount: " + amount);


        // 여기서 금액이 더해지게 하기
        addToDonation(donationId, amount);
        model.addAttribute("donationId", donationId);
        // Optional<Donation> donation = donationService.getDonationById(dontaionId);
        // 닉네임이 실제로 반영되도록(결제 상태를 true로 수정)
        List<NicknameDonation> nicknameDonations = nicknameDonationService.findByDonationId(donationId);
        for (NicknameDonation nicknameDonation : nicknameDonations) {
            nicknameDonation.setStatus(true);
        }
        
        return "success";
    }

    // 결제 정보 조회 (ID로)
    @GetMapping("/payments/{id}")
    public ResponseEntity<Payments> getPaymentById(@PathVariable Long id) {
        Optional<Payments> payment = paymentsService.getPaymentById(id);
        return payment.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 모든 결제 정보 조회
    public List<Payments> getPayments() {
        return paymentsService.getPayments();
    }

}
