package com.hey.givumethemoney.repository;

import com.hey.givumethemoney.domain.MemberDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberDomain, Long>{
    MemberDomain findByEmail(String email);
}
