package com.hey.givumethemoney;

import com.hey.givumethemoney.repository.JPADonationRepository;
import com.hey.givumethemoney.service.DonationApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    private final JPADonationRepository donationRepository;

    @Autowired
    public SpringConfig(JPADonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    @Bean
    public DonationApplicationService donationApplicationService() {
        //return new MemberService(memberRepository());
        return new DonationApplicationService(donationRepository);
    }
}
