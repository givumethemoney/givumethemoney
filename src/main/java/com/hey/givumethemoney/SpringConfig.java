package com.hey.givumethemoney;

import com.hey.givumethemoney.repository.JPAPaymentsRepository;
import com.hey.givumethemoney.service.PaymentsApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    private final JPAPaymentsRepository paymentsRepository;

    @Autowired
    public SpringConfig(JPAPaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    @Bean
    public PaymentsApplicationService paymentsApplicationService() {
        //return new MemberService(memberRepository());
        return new PaymentsApplicationService(paymentsRepository);
    }
}
