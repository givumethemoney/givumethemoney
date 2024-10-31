package com.example.demo.dto;

import com.example.demo.entity.MemberEntity;
import com.example.demo.entity.UserType;
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
    private UserType userType;

    //lombok 어노테이션으로 getter,setter,생성자,toString 메서드 생략 가능

    public static MemberDTO toMemberDTO(MemberEntity memberEntity){
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(memberEntity.getId());
        memberDTO.setEmail(memberEntity.getEmail());
        memberDTO.setPassword(memberEntity.getPassword());
        memberDTO.setUserName(memberEntity.getUserName());
        memberDTO.setCompanyName(memberEntity.getCompanyName());
        memberDTO.setUserType(memberEntity.getUserType());

        return memberDTO;
    }
}