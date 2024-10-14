package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Donation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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
