package com.hey.givumethemoney.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "ocrResult")
public class NaverOCR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin_name", nullable = false)
    private String originName;

    @Column(name = "saved_name", nullable = false)
    private String savedName;

    @Column(name = "saved_path", nullable = false)
    private String savedPath;

    @Column(name = "donation_id", nullable = false)
    private Long donationId;

    @Builder
    public NaverOCR(Long id, String originName, String savedName, String savedPath, Long donationId) {
        this.id = id;
        this.originName = originName;
        this.savedName = savedName;
        this.savedPath = savedPath;
        this.donationId = donationId;
    }
}
