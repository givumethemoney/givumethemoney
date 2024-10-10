package com.hey.givumethemoney.Config;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.WaitingDonation;
import com.hey.givumethemoney.repository.DonationRepository;
import com.hey.givumethemoney.repository.JPADonationRepository;
import com.hey.givumethemoney.repository.WaitingDonationRepository;
import com.hey.givumethemoney.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DonationConfig {

    private final DonationRepository donationRepository;
    private final WaitingDonationRepository waitingDonationRepository;

    @Autowired
    public DonationConfig(DonationRepository donationRepository, WaitingDonationRepository waitingDonationRepository) {
        this.donationRepository = donationRepository;
        this.waitingDonationRepository = waitingDonationRepository;
    }

    @Bean
    public DonationService donationService() {
        return new DonationService(donationRepository, waitingDonationRepository);
    }
}
