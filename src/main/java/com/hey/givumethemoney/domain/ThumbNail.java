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
@Table(name = "thumbnail")
public class ThumbNail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saved_name", nullable = false)
    private String savedName;

    @Column(name = "thumb_url", nullable = true)
    private String thumbUrl;

    @Column(name = "donation_id", nullable = false)
    private Long donationId;

    @Column(name = "img_id", nullable = false)
    private Long imgId;

    @Builder
    public ThumbNail(Long id, String savedName, 
                 Long donationId, String thumbUrl, Long imgId) {
        this.id = id;
        this.savedName = savedName;
        this.thumbUrl = thumbUrl;
        this.donationId = donationId;
        this.imgId = imgId;
    }

}
