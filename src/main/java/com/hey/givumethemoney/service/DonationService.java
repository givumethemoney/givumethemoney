package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.WaitingDonation;
import com.hey.givumethemoney.repository.DonationRepository;
import com.hey.givumethemoney.repository.WaitingDonationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    public List<WaitingDonation> getWaitingDonationsByUserId(String userId) {
        List<WaitingDonation> donations = getWaitingDonations();
        List<WaitingDonation> result = new ArrayList<>();

        for (WaitingDonation d : donations) {
            if (d.getUserId().equals(userId)) {
                result.add(d);
            }
        }

        return result;
    }

    public List<Donation> getDonationsByUserId(String userId) {
        List<Donation> donations = getDonations();
        List<Donation> result = new ArrayList<>();

        for (Donation d : donations) {
            if (d.getUserId().equals(userId)) {
                result.add(d);
            }
        }

        return result;
    }

    public void deleteWaitingDonationById(Long id) {
        waitingDonationRepository.deleteById(id);
    }
}