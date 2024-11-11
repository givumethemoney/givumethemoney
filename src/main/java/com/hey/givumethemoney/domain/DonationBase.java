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

    @Column(name = "title", nullable = false)
    protected String title;

    @Column(name = "start_date", nullable = false)
    protected LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    protected LocalDate endDate;

    @Column(name = "goal", nullable = false)
    protected int goal;

    @Column(name = "current_amount", nullable = false)
    protected int currentAmount;

    @Column(name = "descript", nullable = false)
    protected String description;

    @Column(name = "participant", nullable = false)
    protected int participant;

    @Column(name = "enter_name", nullable = false)
    protected String enterName;

    @Column(name = "is_confirmed", nullable = false)
    protected boolean isConfirmed = false;

    @Column(name = "user_id", nullable = false)
    protected String userId;

//    //@Builder
    public DonationBase(String title, LocalDate startDate, LocalDate endDate, int goal, int currentAmount, String description, int participant, String enterName, String userId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goal = goal;
        this.currentAmount = currentAmount;
        this.description = description;
        this.participant = participant;
        this.enterName = enterName;
        this.userId = userId;
    }
}