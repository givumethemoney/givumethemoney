package com.hey.givumethemoney.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "waiting_donation")
@NoArgsConstructor
@SuperBuilder
public class WaitingDonation extends DonationBase {

    //@Builder
    public WaitingDonation(String title, LocalDate startDate, LocalDate endDate, int goal, int currentAmount, String description, int participant, String enterName, byte[] image, boolean isConfirmed, String userId) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goal = goal;
        this.currentAmount = currentAmount;
        this.description = description;
        this.participant = participant;
        this.enterName = enterName;
        this.image = image;
        this.isConfirmed = isConfirmed;
        this.userId = userId;
    }
}
