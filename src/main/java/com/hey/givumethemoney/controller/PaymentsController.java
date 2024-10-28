package com.hey.givumethemoney.controller;

import org.codehaus.groovy.runtime.dgmimpl.arrays.IntegerArrayGetAtMetaMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;


import com.hey.givumethemoney.controller.*;
import com.hey.givumethemoney.domain.*;
import com.hey.givumethemoney.service.*;

import java.util.List;
import java.util.Optional; 

@Controller
public class PaymentsController {

    PaymentsService paymentsService;

    // 생성자 주입
    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @GetMapping("/payments")
    public String payments () {
        return "payments";
    }

    @GetMapping("/setAmount")
    public String widget(
        @RequestParam(name = "amount", required = false) Integer amount,
        @RequestParam(name = "customAmount", required = false) Integer customAmount,
        Model model
    ) {
        System.out.println("amount: " + amount);
        System.out.println("coustomAmount: " + customAmount);
        int finalAmount = (customAmount != null) ? customAmount : amount;
        System.out.println("finalAmount: " + finalAmount);

        Payments payment = new Payments();
        payment.setAmount(finalAmount);
        model.addAttribute("payment", payment);
        
        // 선택된 금액이나 직접 입력한 금액을 처리하는 로직
        System.out.println("\n\n최종 금액: " + finalAmount);
        System.out.println("\n\n\nwidget.html로 넘어갑니다...\n\n\n");

        // 처리 후 "widget.html" 페이지로 이동
        return "widget";
    }

    // 결제 정보 저장
    @PostMapping
    public Payments createPayment(@RequestBody Payments payment) {
        return paymentsService.savePayments(payment);
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
        @RequestParam(name = "paymentType", required = false) String paymentType // paymentType은 OPTIONAL하게 처리
    ) {
        if (paymentType == null) {
            paymentType = "NORMAL";
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
    @GetMapping
    public List<Payments> getPayments() {
        return paymentsService.getPayments();
    }

}
