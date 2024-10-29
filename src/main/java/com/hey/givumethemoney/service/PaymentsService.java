package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Payments;
import com.hey.givumethemoney.repository.JPAPaymentsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentsService {

    private final JPAPaymentsRepository paymentsRepository;

    @Autowired
    public PaymentsService(JPAPaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    // 결제 정보 저장
    public Payments savePayments(Payments payments) {
        return paymentsRepository.save(payments);
    }

    public List<Payments> getPayments() {
        return paymentsRepository.findAll();
    }

    public List<Payments> findByUserId(Long userId) {
        return paymentsRepository.findByUserId(userId);
    }

    public List<Payments> findByDonationId(Long donId) {
        return paymentsRepository.findByDonationId(donId);
    }

    public Optional<Payments> getPaymentById(Long id)
    {
        return paymentsRepository.findById(id);
    }

}
