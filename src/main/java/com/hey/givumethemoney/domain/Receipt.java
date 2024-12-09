package com.hey.givumethemoney.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "receipt")
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin_name", nullable = false)
    private String originName;

    @Column(name = "saved_name", nullable = false)
    private String savedName;

    @Column(name = "donation_id", nullable = false)
    private Long donationId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;


    @Builder
    public Receipt(Long id, String originName, String savedName, Long donationId, String imageUrl) {
        this.id = id;
        this.originName = originName;
        this.savedName = savedName;
        this.donationId = donationId;
        this.imageUrl = imageUrl;
    }
}
