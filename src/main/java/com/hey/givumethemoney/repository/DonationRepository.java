package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.Image;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Override
    Optional<Donation> findById(Long id);
    List<Donation> findAllByEndDateAfter(LocalDate date);// 종료 날짜가 현재 날짜 이후인 기부
    // 최신 데이터 3개를 가져오는 쿼리
    // Pageable을 사용하는 방식으로 수정
    @Query("SELECT d FROM Donation d ORDER BY d.startDate DESC")
    List<Donation> findTop3Donations(Pageable pageable);
//
//    @Query(value = "SELECT * FROM donation_confirmed ORDER BY start_date DESC LIMIT 3", nativeQuery = true)
//    List<Donation> findTop3Donations();
}

