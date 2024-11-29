package com.hey.givumethemoney.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.hey.givumethemoney.dto.CustomUserDetails;
import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.repository.MemberRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username)
                .map(CustomUserDetails::new) // 성공적으로 사용자 정보를 로드하면 CustomUserDetails 생성
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
    }
}
