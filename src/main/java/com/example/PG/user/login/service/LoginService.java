package com.example.PG.user.login.service;

import com.example.PG.user.member.domain.Member;
import com.example.PG.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member login(String loginId, String password) {
        return memberRepository.findByLoginId(loginId)
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .orElse(null);
    }
}
