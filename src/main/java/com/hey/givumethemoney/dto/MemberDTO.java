package com.hey.givumethemoney.dto;

import com.hey.givumethemoney.domain.MemberDomain;
import com.hey.givumethemoney.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "password") // 비밀번호 제외
public class MemberDTO {
    private Long id;
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자리 이상이어야 합니다.")

    private String password;
    private String userName;
    private String companyName;
    private Role role;

    // Domain -> DTO 변환 메서드
    public static MemberDTO fromMemberDomain(MemberDomain domain) {
        MemberDTO dto = new MemberDTO();
        dto.setId(domain.getId());
        dto.setEmail(domain.getEmail());
        dto.setPassword(null); // 보안상 비밀번호는 설정하지 않음
        dto.setUserName(domain.getUserName());
        dto.setCompanyName(domain.getCompanyName());
        dto.setRole(domain.getRole());
        return dto;
    }
}