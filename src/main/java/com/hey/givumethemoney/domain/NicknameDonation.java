package com.hey.givumethemoney.domain;

import groovy.transform.builder.Builder;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class NicknameDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation; // 연관된 기부

    @Column(name = "nickname", nullable = false)
    private String nickname; // 닉네임

    @Column(name = "amount", nullable = false)
    private int amount;      // 닉네임 기부 금액

    @Column(name = "payment_status")
    private boolean status = false; // 결제 상태

    @Builder
    public NicknameDonation(Donation donation, String nickname, int amount, boolean status) {
        this.donation = donation;
        this.nickname = nickname;
        this.amount = amount;
        this.status = status;
    }
}

