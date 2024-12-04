package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.Donation;
import com.hey.givumethemoney.domain.Image;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Override
    Optional<Donation> findById(Long id);  
    
}
