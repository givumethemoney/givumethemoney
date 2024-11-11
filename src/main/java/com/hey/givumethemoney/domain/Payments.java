package com.hey.givumethemoney.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    //결제할 때 사용한 통화
    @Column(name = "currency")
    private String currency; // 브랜드 페이는 "KRW"만 지원

    @Column(name = "amount", nullable = false) // 결제 금액
    private int amount;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    //구매상품입니다. 예를 들면 생수 외 1건 같은 형식입니다. 최대 길이는 100자입니다.
    @Column(name = "order_name")
    private String orderName;

    //결제수단입니다. 카드, 가상계좌, 간편결제, 휴대폰, 계좌이체, 
    // 문화상품권, 도서문화상품권, 게임문화상품권 중 하나입니다.
    @Column(name = "method", nullable = true)
    private String method;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_name")
    private String customerName;  // 구매자 이름

    @Column(name = "customer_mobile_phone")
    private String customerMobilePhone;

    // 결제가 일어난 날짜와 시간 정보입니다. 
    // yyyy-MM-dd'T'HH:mm:ss±hh:mm ISO 8601 형식입니다.
    // (e.g. 2022-01-01T00:00:00+09:00)
    @Column(name = "requested_at")
    private String requestedAt;

    @Column(name = "donation_id")
    private Long donationId;

    @Column(name = "user_id")
    private String userId;

    // 에스크로 사용 여부
    @Column(name = "use_escrow")
    private boolean useEscrow;


    @Builder
    public Payments(String orderId, String currency, int amount, String orderName, String customerEmail,
                    String customerName, String customerMobilePhone, Long donationId,
                    String paymentKey) {
        this.orderId = orderId;
        this.currency = currency;
        this.amount = amount;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.customerMobilePhone = customerMobilePhone;
        this.donationId = donationId;
        // this.paymentStatus = paymentStatus;
    }

    
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
