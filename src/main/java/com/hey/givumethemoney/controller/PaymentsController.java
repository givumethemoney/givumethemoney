package com.hey.givumethemoney.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    // @GetMapping("/success")
    // public Success success (@PathVariable String paymentKey, int amount, String orderId) {
    //    this.orderId = orderId;
    //     payments.paymentKey = paymentKey;
    //     this.amount = amount;
    //     return "success/{:}";
    // }

}
