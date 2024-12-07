package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.NicknameDonation;
import com.hey.givumethemoney.repository.NicknameDonationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class NicknameDonationService {

    private final NicknameDonationRepository nicknameDonationRepository;

    @Autowired
    public NicknameDonationService(NicknameDonationRepository nicknameDonationRepository) {
        this.nicknameDonationRepository = nicknameDonationRepository;
    }

    public List<NicknameDonation> findByDonationId(Long id) {
        return nicknameDonationRepository.findByDonationId(id);
    }
}