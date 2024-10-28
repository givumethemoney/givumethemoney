package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Inquiry;
import com.hey.givumethemoney.repository.JPAInquiryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public class InquiryService {
    private final JPAInquiryRepository inquiryRepository;

    public InquiryService(JPAInquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    public Inquiry saveInquiry(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    public List<Inquiry> getInquiry() {
        return inquiryRepository.findAll();
    }

    public Optional<Inquiry> getInquiryById(Long id)
    {
        return inquiryRepository.findById(id);
    }

}
