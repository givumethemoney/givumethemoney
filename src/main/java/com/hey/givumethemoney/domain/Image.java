package com.hey.givumethemoney.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "image")
public class Image implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origin_name", nullable = false)
    private String originName;

    @Column(name = "saved_name", nullable = false)
    private String savedName;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    @Column(name = "thumb_url", nullable = false)
    private String thumbUrl;

    @Column(name = "donation_id", nullable = false)
    private Long donationId;

    @Builder
    public Image(Long id, String originName, String savedName, 
                 String imgUrl, Long donationId, String thumbUrl) {
        this.id = id;
        this.originName = originName;
        this.savedName = savedName;
        this.imgUrl = imgUrl;
        this.donationId = donationId;
        this.thumbUrl = thumbUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

}