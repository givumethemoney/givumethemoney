package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Inquiry;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JPAInquiryRepository extends JpaRepository<Inquiry, Long>{

    @Override
    void flush();

    @Override
    <S extends Inquiry> S saveAndFlush(S entity);

    @Override
    <S extends Inquiry> List<S> findAll(Example<S> example);

    @Override
    <S extends Inquiry> Page<S> findAll(Example<S> example, Pageable pageable);

    @Override
    <S extends Inquiry> S save(S entity);

    @Override
    Optional<Inquiry> findById(Long aLong);

    @Override
    long count();

}

