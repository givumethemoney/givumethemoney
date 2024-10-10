package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Payments;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface JPAPaymentsRepository extends JpaRepository<Payments, Long>{
    @Override
    void flush();

    @Override
    <S extends Payments> S saveAndFlush(S entity);

    @Override
    <S extends Payments> List<S> findAll(Example<S> example);

    @Override
    <S extends Payments> Page<S> findAll(Example<S> example, Pageable pageable);

    @Override
    <S extends Payments> S save(S entity);

    @Override
    Optional<Payments> findById(Long aLong);

    @Override
    long count();
}
