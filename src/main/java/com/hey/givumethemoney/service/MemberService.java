package com.hey.givumethemoney.service;

import com.hey.givumethemoney.dto.MemberDTO;
import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Primary //기본 UserDetailService로 사용
// MemberService: 비즈니스 로직(회원가입, 사용자 조회 등)을 처리
// 인증이 아닌, 애플리케이션 로직에서 사용자 정보를 다룰 때 사용
// Spring Security와 관계없이 자유롭게 사용할 수 있음
public class MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 초기화
    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    @Transactional
    public void join(MemberDTO memberDTO) {
        // 이메일 중복 체크
        if (memberRepository.findByEmail(memberDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        // 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        System.out.println("Encoded password to be saved: " + encodedPassword);
        memberDTO.setPassword(encodedPassword);

        // DTO -> Domain 변환 후 저장
        MemberDomain memberDomain = MemberDomain.toMemberDomain(memberDTO);
        memberRepository.save(memberDomain);
    }

    // 이메일로 사용자 찾기 (회원가입 관련 작업용)
    public MemberDomain findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
    }
}