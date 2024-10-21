package com.hey.givumethemoney.domain;

import com.hey.givumethemoney.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "users") //database에 해당 이름의 테이블 생성
public class MemberDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "company_name")
    private String companyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    public static MemberDomain toMemberDomain(MemberDTO memberDTO){
        MemberDomain memberDomain = new MemberDomain();
        memberDomain.setId(memberDTO.getId());
        memberDomain.setEmail(memberDTO.getEmail());
        memberDomain.setPassword(memberDTO.getPassword());
        memberDomain.setUserName(memberDTO.getUserName());
        memberDomain.setCompanyName(memberDTO.getCompanyName());
        memberDomain.setRole(memberDTO.getRole());

        return memberDomain;
    }
}