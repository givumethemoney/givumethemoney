package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Donation;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JPADonationRepository extends JpaRepository<Donation, Long> {

    @Override
    void flush();

    @Override
    <S extends Donation> S saveAndFlush(S entity);

    @Override
    <S extends Donation> List<S> findAll(Example<S> example);

    @Override
    <S extends Donation> Page<S> findAll(Example<S> example, Pageable pageable);

    @Override
    <S extends Donation> S save(S entity);

    @Override
    Optional<Donation> findById(Long aLong);

    @Override
    long count();
}
