package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.WaitingDonation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaitingDonationRepository extends JpaRepository<WaitingDonation, Long> {

    @Override
    <S extends WaitingDonation> S save(S entity);

    @Override
    Optional<WaitingDonation> findById(Long aLong);

    @Override
    void deleteById(Long aLong);

    @Override
    List<WaitingDonation> findAll(Sort sort);
}
