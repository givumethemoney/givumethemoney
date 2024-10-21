package com.hey.givumethemoney.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.hey.givumethemoney.controller.*;
import com.hey.givumethemoney.domain.*;
import com.hey.givumethemoney.service.*;

@Controller
public class PaymentsController {

    PaymentsApplicationService paymentsApplicationService;

    @GetMapping("/payments")
    public String payments () {
        return "payments";
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

}
