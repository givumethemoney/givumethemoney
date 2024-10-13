package com.hey.givumethemoney.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class DonationBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected Long id;

    @Column(name = "title", nullable = false)
    protected String title;

    @Column(name = "start_date", nullable = false)
    protected LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    protected LocalDate endDate;

    @Column(name = "goal", nullable = false)
    protected int goal;

    @Column(name = "current_amount")
    protected int currentAmount;

    @Column(name = "descript")
    protected String description;

    @Column(name = "participant")
    protected int participant;

    @Column(name = "enter_name", nullable = false)
    protected String enterName;

    @Column(name = "is_confirmed")
    protected boolean isConfirmed;

    @Column(name = "user_id")
    protected String userId;

//    //@Builder
    public DonationBase(String title, LocalDate startDate, LocalDate endDate, int goal, int currentAmount, String description, int participant, String enterName, boolean isConfirmed, String userId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goal = goal;
        this.currentAmount = currentAmount;
        this.description = description;
        this.participant = participant;
        this.enterName = enterName;
        this.isConfirmed = isConfirmed;
        this.userId = userId;
    }
}