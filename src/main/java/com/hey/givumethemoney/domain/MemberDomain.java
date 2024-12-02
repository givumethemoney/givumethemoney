package com.hey.givumethemoney.domain;

import com.hey.givumethemoney.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "member")
public class MemberDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private Role role = Role.COMPANY;

    public void setEncodedPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // MemberDTO -> MemberDomain 변환 메서드
    public static MemberDomain toMemberDomain(MemberDTO dto) {
        MemberDomain domain = new MemberDomain();
        domain.email = dto.getEmail();
        domain.password = dto.getPassword();
        domain.userName = dto.getUserName();
        domain.companyName = dto.getCompanyName();
        domain.role = dto.getRole() != null ? dto.getRole() : Role.COMPANY;
        return domain;
    }
}
