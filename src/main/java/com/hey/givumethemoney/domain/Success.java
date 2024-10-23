package com.hey.givumethemoney.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "success")
@Getter
@Setter
@NoArgsConstructor
public class Success {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "amount", nullable = false) // 결제 금액
    private int amount;

    @Column(name = "paymentKey")
    private String paymentKey;

    @Column(name = "orderId")
    private String orderId;

    @Builder
    public Success(String orderId, int amount, String paymentKey) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentKey = paymentKey;
    }
}
