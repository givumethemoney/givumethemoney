package com.hey.givumethemoney.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "waiting_donation")
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
public class WaitingDonation extends DonationBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected Long id;

    //@Builder
    public WaitingDonation(String title, LocalDate startDate, LocalDate endDate, int goal, int currentAmount, String description, int participant, String enterName, String userId) {
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
