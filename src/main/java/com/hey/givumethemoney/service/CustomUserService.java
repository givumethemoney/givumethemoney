package com.hey.givumethemoney.service;

import com.hey.givumethemoney.domain.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.dto.CustomUserDetails;

@Service
// UserDetailsService는 자바 표준이 아니라 Spring Security에서 제공하는 인터페이스
// 인증 과정에서 사용자 정보를 관리하기 위해 중요한 역할
// CustomUserService: Spring Security 인증/권한 관리와 관련된 로직을 처리
public class CustomUserService implements UserDetailsService {

    private final MemberService memberService;

    public CustomUserService(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // MemberService의 findByEmail 사용
        MemberDomain member = memberService.findByEmail(username);

        if (member == null) {
        throw new UsernameNotFoundException("User not found");
        }
        
        // CustomUserDetails로 변환하여 반환
        return new CustomUserDetails(member);
    }

    public Role getRole() throws UsernameNotFoundException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = userDetails.getUsername();

        MemberDomain member = memberService.findByEmail(userEmail);

        return member.getRole();
    }

    public String getEmail() throws UsernameNotFoundException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
