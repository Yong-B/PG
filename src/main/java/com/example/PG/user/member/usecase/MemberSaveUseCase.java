package com.example.PG.user.member.usecase;

import com.example.PG.user.member.domain.Member;

public interface MemberSaveUseCase {
    Member save(Member member);

    boolean isLoginIdDuplicate(String loginId);
    boolean isEmailDuplicate(String email);

}
