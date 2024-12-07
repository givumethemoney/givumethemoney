package com.hey.givumethemoney.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hey.givumethemoney.domain.NaverOCR;

@Repository
public interface NaverOCRRepository extends JpaRepository<NaverOCR, Long>{

}
