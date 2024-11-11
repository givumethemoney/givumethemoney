package com.hey.givumethemoney.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.ui.Model;


import com.hey.givumethemoney.domain.*;
import com.hey.givumethemoney.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
public class PaymentsController {
    
    PaymentsService paymentsService;
    DonationService donationService;

    // 생성자 주입
    @Autowired
    public PaymentsController(PaymentsService paymentsService, DonationService donationService) {
        this.paymentsService = paymentsService;
        this.donationService = donationService;
    }

    @GetMapping("/payments")
    public String payments (
        @RequestParam(name = "donationId") Long dontaionId,
        Model model
    ) {
        model.addAttribute("donationId", dontaionId);
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
        @RequestParam(name = "donationId") Long dontaionId,
        Model model
    ) {
        System.out.println("amount: " + amount);
        System.out.println("coustomAmount: " + customAmount);
        int finalAmount = (customAmount != null) ? customAmount : amount;
        System.out.println("finalAmount: " + finalAmount);

        Payments payment = new Payments();
        payment.setAmount(finalAmount);
        payment.setDonationId(dontaionId);
        model.addAttribute("payment", payment);
        
        // System.out.println("\n\n최종 금액: " + finalAmount);
        // System.out.println("\n\n\nwidget.html로 넘어갑니다...\n\n\n");

        return "widget";
    }

    // 결제 정보 저장
    @PostMapping
    public Payments createPayment(@RequestBody Payments payment) {

        return paymentsService.savePayments(payment);
    }

    // 결제 금액 더하기
    private void addAmount(Long donationId, int amount) {
        Optional<Donation> donation = donationService.getDonationById(donationId);

        if (donation.isPresent()) {
            donation.get().setCurrentAmount(donation.get().getCurrentAmount() + amount);
            donation.get().setParticipant(donation.get().getParticipant() + 1);
        }
    }

    @GetMapping("/success")
    // paymentType=NORMAL
    // orderId=UwWzfop68Q-vW68WqF23b
    // paymentKey=tgen_202410211645296hdg0
    // amount=50000
    public String success (
        @RequestParam(name = "paymentKey") String paymentKey, 
        @RequestParam(name = "amount") int amount, 
        @RequestParam(name = "orderId") String orderId, 
        @RequestParam(name = "paymentType", required = false) String paymentType, // paymentType은 OPTIONAL하게 처리
        @RequestParam(name = "donationId") Long dontaionId
    ) {
        if (paymentType == null) {
            paymentType = "NORMAL";
        }
        // 여기서 금액이 더해지게 하기
        addAmount(dontaionId, amount);


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
    @GetMapping
    public List<Payments> getPayments() {
        return paymentsService.getPayments();
    }

}
