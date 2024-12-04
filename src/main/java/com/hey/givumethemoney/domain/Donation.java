package com.hey.givumethemoney.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "donation_confirmed")
@NoArgsConstructor
@SuperBuilder
@Getter
public class Donation extends DonationBase {

    @Id
    @Column(name = "id", nullable = false)
    protected Long id;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL)
    @Builder.Default
    private List<NicknameDonation> nicknameDonations = new ArrayList<>();

    //@Builder
    public Donation(WaitingDonation waitingDonation) {
        this.title = waitingDonation.getTitle();
        this.startDate = waitingDonation.getStartDate();
        this.endDate = waitingDonation.getEndDate();
        this.goal = waitingDonation.getGoal();
        this.currentAmount = waitingDonation.getCurrentAmount();
        this.description = waitingDonation.getDescription();
        this.participant = waitingDonation.getParticipant();
        this.enterName = waitingDonation.getEnterName();
        this.isConfirmed = !waitingDonation.isConfirmed();
        this.userId = waitingDonation.getUserId();

        this.id = waitingDonation.getId();
    }
}
