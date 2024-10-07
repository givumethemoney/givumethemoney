package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.repository.JPADonationRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class DonationApplicationService {

    private final JPADonationRepository donationRepository;

    public DonationApplicationService(JPADonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public List<Donation> getDonations() {
        return donationRepository.findAll();
    }

    public Optional<Donation> getDonationById(Long id)
    {
        return donationRepository.findById(id);
    }

}
