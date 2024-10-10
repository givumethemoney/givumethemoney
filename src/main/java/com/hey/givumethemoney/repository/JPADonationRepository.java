package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.DonationBase;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JPADonationRepository<T extends DonationBase> extends JpaRepository<T, Long> {

    @Override
    <S extends T> S save(S entity);

    @Override
    Optional<T> findById(Long aLong);

    @Override
    void deleteById(Long aLong);

    @Override
    List<T> findAll(Sort sort);
}
