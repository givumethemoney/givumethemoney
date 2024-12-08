package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.NicknameDonation;
import com.hey.givumethemoney.domain.WaitingDonation;
import com.hey.givumethemoney.repository.DonationRepository;
import com.hey.givumethemoney.repository.NicknameDonationRepository;
import com.hey.givumethemoney.repository.WaitingDonationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class DonationRecommendationService {

    @Autowired
    private DonationRepository donationRepository;

    // @Autowired
    // private ProjectRepository projectRepository;

    // public BigDecimal getSuggestedDonationAmount(int projectId) {
    //     // 기부 내역을 기반으로 해당 프로젝트와 비슷한 기부 금액 추천
    //     List<Donation> donations = donationRepository.findByDonationId(projectId);
        
    //     if (donations.isEmpty()) {
    //         return BigDecimal.ZERO; // 기부자가 없다면 0을 반환하거나 기본 값을 반환
    //     }
    //     BigDecimal averageAmount = donations.stream()
    //         .map(Donation::getAmount)
    //         .reduce(BigDecimal.ZERO, BigDecimal::add)
    //         .divide(BigDecimal.valueOf(donations.size()), RoundingMode.HALF_UP);
        
    //     BigDecimal minSuggestedAmount = averageAmount.subtract(BigDecimal.valueOf(1000)); // 1000원 정도 차감
    //     BigDecimal maxSuggestedAmount = averageAmount.add(BigDecimal.valueOf(1000)); // 1000원 정도 추가
        
    //     return averageAmount;
    // }
}
