package com.hey.givumethemoney.dto;

import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.domain.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class MemberDTO { //회원 정보를 필드로 정의
    private Long id;
    private String email;
    private String password;
    private String userName;
    private String companyName;
    private Role role;

    public static MemberDTO toMemberDTO(MemberDomain memberDomain){
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberDomain.getId());
        memberDTO.setEmail(memberDomain.getEmail());
        memberDTO.setPassword(memberDomain.getPassword());
        memberDTO.setUserName(memberDomain.getUserName());
        memberDTO.setCompanyName(memberDomain.getCompanyName());
        memberDTO.setRole(memberDomain.getRole());

        return memberDTO;
    }
}