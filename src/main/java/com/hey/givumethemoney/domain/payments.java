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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "payment_time", nullable = false)
    private LocalDateTime paymentTime;

    @Column(name = "donation_id")
    private int donationId;

    @Column(name = "user_id")
    private String userId;

    @Builder
    public Payments(int price, String method, LocalDateTime paymentTime, int donationId, String userId) {
        this.price = price;
        this.method = method;
        this.paymentTime = paymentTime;
        this.donationId = donationId;
        this.userId = userId;
    }
}
