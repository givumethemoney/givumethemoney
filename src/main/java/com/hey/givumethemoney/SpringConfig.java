package com.hey.givumethemoney;

import com.hey.givumethemoney.repository.*;
import com.hey.givumethemoney.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    private final JPAPaymentsRepository paymentsRepository;
    private final JPAInquiryRepository inquiryRepository;

    @Autowired
    public SpringConfig(JPAPaymentsRepository paymentsRepository, JPAInquiryRepository inquiryRepository) {
        this.paymentsRepository = paymentsRepository;
        this.inquiryRepository = inquiryRepository;
    }

    @Bean
    public PaymentsService paymentsService() {
        return new PaymentsService(paymentsRepository);
    }

    @Bean
    public InquiryService inquiryRService() {
        return new InquiryService(inquiryRepository);
    }
}
