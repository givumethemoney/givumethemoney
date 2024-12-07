package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.NicknameDonation;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NicknameDonationRepository extends JpaRepository<NicknameDonation, Long> {
    // 특정 Donation ID로 닉네임 기부 내역 조회
    List<NicknameDonation> findByDonationId(Long Id);

    // status가 true인 닉네임만 가져오는 메서드
    List<NicknameDonation> findByDonationIdAndStatusTrue(Long donationId);
}
