package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Payments;
import com.hey.givumethemoney.repository.JPAPaymentsRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class PaymentsApplicationService {

    private final JPAPaymentsRepository paymentsRepository;

    public PaymentsApplicationService(JPAPaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    public Payments savePayments(Payments payments) {
        return paymentsRepository.save(payments);
    }

    public List<Payments> getPayments() {
        return paymentsRepository.findAll();
    }

    public Optional<Payments> getPaymentsById(Long id)
    {
        return paymentsRepository.findById(id);
    }

}
