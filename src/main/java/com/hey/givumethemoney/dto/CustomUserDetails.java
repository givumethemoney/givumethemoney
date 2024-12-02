package com.hey.givumethemoney.dto;

import com.hey.givumethemoney.domain.MemberDomain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * CustomUserDetails는 Spring Security의 UserDetails를 구현하여
 * MemberDomain 객체를 기반으로 사용자 인증 및 권한 정보를 제공
 */
public class CustomUserDetails implements UserDetails {
    private final MemberDomain memberDomain;
    private final Collection<? extends GrantedAuthority> authorities;

    // 생성자
    public CustomUserDetails(MemberDomain memberDomain) {
        this.memberDomain = memberDomain;

        // 사용자의 Role 기반으로 권한 설정
        this.authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + memberDomain.getRole().name())
        );
    }

    /**
     * 사용자 권한 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * 사용자 이메일 반환 (추가적인 사용자 정보 제공)
     */
    public String getEmail() {
        return memberDomain.getEmail();
    }

    /**
     * 사용자 비밀번호 반환
     */
    @Override
    public String getPassword() {
        return memberDomain.getPassword();
    }

    /**
     * 사용자 이름 반환
     */
    @Override
    public String getUsername() {
        return memberDomain.getUserName();
    }

    /**
     * 사용자 회사명 반환 (추가적인 사용자 정보 제공)
     */
    public String getCompanyName() {
        return memberDomain.getCompanyName();
    }

    /**
     * 계정이 만료되지 않았음을 반환
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부를 실제 값으로 변경하려면 memberDomain에 필드 추가
    }

    /**
     * 계정이 잠겨있지 않았음을 반환
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부를 실제 값으로 변경하려면 memberDomain에 필드 추가
    }

    /**
     * 비밀번호가 만료되지 않았음을 반환
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 여부를 실제 값으로 변경하려면 memberDomain에 필드 추가
    }

    /**
     * 계정이 활성화 상태임을 반환
     */
    @Override
    public boolean isEnabled() {
        return true; // 계정 활성 여부를 실제 값으로 변경하려면 memberDomain에 필드 추가
    }
}
