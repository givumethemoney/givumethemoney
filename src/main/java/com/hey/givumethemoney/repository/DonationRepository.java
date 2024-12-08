package com.hey.givumethemoney.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hey.givumethemoney.domain.Donation;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Override
    Optional<Donation> findById(Long id);
    List<Donation> findAllByEndDateAfter(LocalDate date);// 종료 날짜가 현재 날짜 이후인 기부
    // 최신 데이터 3개를 가져오는 쿼리
    // Pageable을 사용하는 방식으로 수정
    @Query("SELECT d FROM Donation d ORDER BY d.startDate DESC")
    List<Donation> findTop3Donations(Pageable pageable);

    @Query("SELECT d FROM Donation d WHERE d.title LIKE %:keyword%")
    List<Donation> findByNameContaining(@Param("keyword") String keyword);

}

