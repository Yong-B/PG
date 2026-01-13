package com.example.PG.user.member.controller;

import com.example.PG.user.member.usecase.MemberSaveUseCase;
import com.example.PG.user.member.controller.dto.MemberDto;
import com.example.PG.user.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberSaveUseCase memberSaveUseCase;

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") MemberDto dto) {
        return "member/addMemberForm";
    }
    
    @PostMapping("/add")
    public String save(@Valid @ModelAttribute("member") MemberDto dto, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // 유효성 검사 실패 시 폼으로 다시 돌아가기
        if (bindingResult.hasErrors()) {
            return "member/addMemberForm";
        }

        Member member = dto.memberSave();
        memberSaveUseCase.save(member);
        redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인하세요!");
        System.out.println("회원가입 성공");
        return "redirect:/";
    }

    @GetMapping("/check-login-id")
    @ResponseBody
    public Map<String, Boolean> checkLoginId(@RequestParam String loginId) {
        boolean exists = memberSaveUseCase.isLoginIdDuplicate(loginId);
        return Collections.singletonMap("exists", exists);
    }

    // 이메일 중복 체크
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean exists = memberSaveUseCase.isEmailDuplicate(email);
        return Collections.singletonMap("exists", exists);
    }

    @GetMapping("/check-login")
    @ResponseBody
    public Map<String, Boolean> checkLoginStatus(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("loginMemberId") != null);
        return Collections.singletonMap("loggedIn", loggedIn);
    }
}
