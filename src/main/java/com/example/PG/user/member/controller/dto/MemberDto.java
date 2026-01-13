package com.example.PG.user.member.controller.dto;

import com.example.PG.user.member.domain.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import jakarta.validation.constraints.NotEmpty;

@Getter
@Builder
public class MemberDto {
    @NotEmpty(message = "아이디를 입력하십시오.")
    @Size(min = 3, max = 50, message = "아이디는 3~50글자 사이여야 합니다.")
    private String loginId;

    @NotEmpty(message = "비밀번호를 입력하십시오.")
    @Size(min = 3, message = "비밀번호는 세 글자 이상 입력하세요.")
    private String password;

    @NotEmpty(message = "이름을 입력하십시오.")
    private String name;

    @NotEmpty(message = "이메일을 입력하십시오.")
    @Email(message = "유효한 이메일 형식을 입력하십시오.")
    private String email;

    // 엔티티 변환 메서드
    public Member memberSave() {
        return Member.builder()
                .loginId(this.loginId)
                .password(this.password)
                .name(this.name)
                .email(this.email)
                .build();
    }
}