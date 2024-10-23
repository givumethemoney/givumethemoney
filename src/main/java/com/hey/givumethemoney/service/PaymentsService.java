package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Payments;
import com.hey.givumethemoney.repository.JPAPaymentsRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class PaymentsService {

    private final JPAPaymentsRepository paymentsRepository;

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

    public Optional<Payments> getPaymentById(Long id)
    {
        return paymentsRepository.findById(id);
    }

}
