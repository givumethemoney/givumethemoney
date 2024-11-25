package com.hey.givumethemoney.dto;

import com.hey.givumethemoney.domain.MemberDomain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final MemberDomain memberDomain;

    public CustomUserDetails(MemberDomain memberDomain) {
        this.memberDomain = memberDomain;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + memberDomain.getRole().name()));
    }

    public String getEmail() {
        return memberDomain.getEmail();
    }

    @Override
    public String getPassword() {
        return memberDomain.getPassword();
    }

    @Override
    public String getUsername() {
        return memberDomain.getUserName();
    }

    public String getCompanyName() {
        return memberDomain.getCompanyName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 안됨
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 안됨
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명(비밀번호) 만료 안됨
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화됨
    }
}