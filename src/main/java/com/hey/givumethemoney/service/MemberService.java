package com.hey.givumethemoney.service;

import com.hey.givumethemoney.dto.MemberDTO;
import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;


@Service //스프링이 관리해주는 객체 == 스프링 빈
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository; // 먼저 jpa, mysql dependency 추가
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //회원가입
    public void join(MemberDTO memberDTO) {
        //비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);
        MemberDomain memberDomain = MemberDomain.toMemberDomain(memberDTO);
        memberRepository.save(memberDomain);
    }
    // 이메일로 사용자 찾기
    public MemberDomain findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberDomain memberDomain = memberRepository.findByEmail(email);
        if (memberDomain == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(
                memberDomain.getEmail(),
                memberDomain.getPassword(),
                new ArrayList<>()
        );
    }
}