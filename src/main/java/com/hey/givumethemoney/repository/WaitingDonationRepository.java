package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.WaitingDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WaitingDonationRepository extends JpaRepository<WaitingDonation, Long> {
}
