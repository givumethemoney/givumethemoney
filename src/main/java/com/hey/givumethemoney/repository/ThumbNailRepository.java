package com.hey.givumethemoney.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hey.givumethemoney.domain.ThumbNail;

public interface ThumbNailRepository extends JpaRepository<ThumbNail, Long> {
    
    @Override
    <S extends ThumbNail> S save(S entity);

    // donationId로 썸네일을 찾는 쿼리
    @Query("SELECT t FROM ThumbNail t WHERE t.donationId = :donationId")
    List<ThumbNail> findByDonationId(@Param("donationId") Long donationId);

}
