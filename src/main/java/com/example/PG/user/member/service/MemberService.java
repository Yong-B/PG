package com.example.PG.user.member.service;

import com.example.PG.user.member.usecase.MemberSaveUseCase;
import com.example.PG.user.member.domain.Member;
import com.example.PG.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements MemberSaveUseCase {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean isLoginIdDuplicate(String loginId) {
        return memberRepository.findByLoginId(loginId).isPresent();
    }

    @Override
    public boolean isEmailDuplicate(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    @Override
    public Member save(Member member) {
        // 로그인 ID 중복 검사
        if (isLoginIdDuplicate(member.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 ID입니다.");
        }
        // 이메일 중복 검사
        if (isEmailDuplicate(member.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Member encodedMember = member.encodePassword(passwordEncoder);

        return memberRepository.save(encodedMember);
    }
}