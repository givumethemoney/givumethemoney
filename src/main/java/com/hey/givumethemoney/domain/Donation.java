package com.hey.givumethemoney.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "donation")
@Getter
@Setter
@NoArgsConstructor
public class Donation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "goal", nullable = false)
    private int goal;

    @Column(name = "current_amount")
    private int currentAmount;

    @Column(name = "descript")
    private String descript;

    @Column(name = "participant")
    private int participant;

    @Column(name = "enter_name", nullable = false)
    private String enterName;

    @Column(name = "image")
    @Lob
    private byte[] image;

    @Column(name = "is_confirmed")
    private boolean isConfirmed;

    @Column(name = "user_id")
    private String userId;

    @Builder
    public Donation(String title, LocalDate startDate, LocalDate endDate, int goal, int currentAmount, String descript, int participant, String enterName, byte[] image, boolean isConfirmed, String userId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goal = goal;
        this.currentAmount = currentAmount;
        this.descript = descript;
        this.participant = participant;
        this.enterName = enterName;
        this.image = image;
        this.isConfirmed = isConfirmed;
        this.userId = userId;
    }
}
