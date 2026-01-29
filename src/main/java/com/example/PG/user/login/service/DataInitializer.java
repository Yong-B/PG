package com.example.PG.user.login.service;

import com.example.PG.user.member.domain.Member;
import com.example.PG.user.member.domain.Role;
import com.example.PG.user.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 설정 파일(yml)에서 값을 읽어옵니다. 
    // 값이 없으면 에러가 나거나 기본값(admin1234)을 쓰도록 설정할 수 있습니다.
    @Value("${admin.password:admin1234}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (memberRepository.findByLoginId("admin").isEmpty()) {
            Member admin = Member.builder()
                    .loginId("admin")
                    .password(passwordEncoder.encode(adminPassword))
                    .email("admin@naver.com")
                    .name("운영자")
                    .role(Role.ADMIN)
                    .build();
            memberRepository.save(admin);
            System.out.println(">>> 관리자 계정(admin) 생성 완료!");
        }
        long userCount = memberRepository.count();
        if (userCount < 5001) { // admin 포함 약 5000명 체크
            System.out.println(">>> 가상 유저 5,000명 생성을 시작합니다...");

            List<Member> dummyMembers = new java.util.ArrayList<>();
            String commonPassword = passwordEncoder.encode("pw1234"); // 비밀번호 암호화는 한 번만 해서 재사용

            for (int i = 1; i <= 5000; i++) {
                Member member = Member.builder()
                        .loginId("user" + i)
                        .password(commonPassword)
                        .name("테스트유저" + i)
                        .email("user" + i + "@test.com")
                        .role(Role.USER)
                        .build();
                dummyMembers.add(member);

                // 💡 1,000명씩 묶어서 저장 (성능과 메모리 효율을 위함)
                if (i % 1000 == 0) {
                    memberRepository.saveAll(dummyMembers);
                    dummyMembers.clear();
                    System.out.println(">>> 유저 생성 중... " + i + "명 완료");
                }
            }
            System.out.println(">>> 총 5,000명의 가상 유저 생성 완료!");
        }
    }
}