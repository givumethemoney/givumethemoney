package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.WaitingDonation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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
