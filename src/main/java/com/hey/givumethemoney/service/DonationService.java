package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.WaitingDonation;
import com.hey.givumethemoney.repository.DonationRepository;
import com.hey.givumethemoney.repository.JPADonationRepository;
import com.hey.givumethemoney.repository.WaitingDonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class DonationService {

    private final DonationRepository donationRepository;
    private final WaitingDonationRepository waitingDonationRepository;

    public DonationService(DonationRepository donationRepository, WaitingDonationRepository waitingDonationRepository) {
        this.donationRepository = donationRepository;
        this.waitingDonationRepository = waitingDonationRepository;
    }

    public WaitingDonation saveWaitingDonation(WaitingDonation wadingDonation) {
        return waitingDonationRepository.save(wadingDonation);
    }
    public Donation saveDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public List<WaitingDonation> getWaitingDonations() {
        return waitingDonationRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public List<Donation> getDonations() {
        return donationRepository.findAll();
    }

    public Optional<WaitingDonation> getWaitingDonationById(Long id) {
        return waitingDonationRepository.findById(id);
    }
    public Optional<Donation> getDonationById(Long id) {
        return donationRepository.findById(id);
    }

    public void deleteWaitingDonationById(Long id) {
        waitingDonationRepository.deleteById(id);
    }
}
