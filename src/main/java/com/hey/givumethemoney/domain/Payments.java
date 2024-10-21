package com.hey.givumethemoney.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payments {

    // orderId: "UwWzfop68Q-vW68WqF23b",
    // orderName: "토스 티셔츠 외 2건",
    // successUrl: window.location.origin + "/success.html",
    // failUrl: window.location.origin + "/fail.html",
    // customerEmail: "customer123@gmail.com",
    // customerName: "김토스",
    // customerMobilePhone:
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "currency")
    private String currency; // 브랜드 페이는 "KRW"만 지원

    @Column(name = "amount", nullable = false) // 결제 금액
    private int amount;

    @Column(name = "order_name")
    private String orderName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_name")
    private String customerName;  // 구매자 이름

    @Column(name = "customer_mobile_phone")
    private String customerMobilePhone;

    @Column(name = "success_url")
    private String successUrl;

    @Column(name = "fail_url")
    private String failUrl;

    // @Column(name = "payment_time")
    // private LocalDateTime paymentTime;

    @Column(name = "donation_id")
    private int donationId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "paymentKey")
    private String paymentKey;

    // @Column(name = "payment_status", nullable = false)
    // private String paymentStatus = "pending";  // 결제 상태 (예: pending, success, fail)


    @Builder
    public Payments(String orderId, String currency, int amount, String orderName, String customerEmail,
                    String customerName, String customerMobilePhone, String successUrl, String failUrl, String paymentKey) {
        this.orderId = orderId;
        this.currency = currency;
        this.amount = amount;
        this.orderName = orderName;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.customerMobilePhone = customerMobilePhone;
        this.successUrl = successUrl;
        this.failUrl = failUrl;
        this.paymentKey = paymentKey;
        // this.paymentStatus = paymentStatus;
    }
}
