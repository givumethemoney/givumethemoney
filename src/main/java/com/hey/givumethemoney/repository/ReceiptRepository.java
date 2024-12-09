package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Receipt;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    @Override
    void flush();

    @Override
    <S extends Receipt> S saveAndFlush(S entity);

    @Override
    <S extends Receipt> List<S> findAll(Example<S> example);

    @Override
    <S extends Receipt> S save(S entity);

    @Override
    Optional<Receipt> findById(Long aLong);

    @Override
    long count();

    @Query("SELECT p FROM Receipt p WHERE p.donationId = :donationId")
    List<Receipt> findByDonationId(@Param("donationId") Long donationId);



}
