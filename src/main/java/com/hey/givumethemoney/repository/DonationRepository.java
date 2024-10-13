package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Donation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Override
    <S extends Donation> S save(S entity);

    @Override
    Optional<Donation> findById(Long aLong);

    @Override
    void deleteById(Long aLong);

    @Override
    List<Donation> findAll(Sort sort);
}
