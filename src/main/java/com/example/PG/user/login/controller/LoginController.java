package com.example.PG.user.login.controller;

import com.example.PG.user.login.domain.LoginForm;
import com.example.PG.user.login.service.LoginService;
import com.example.PG.user.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {
    
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm() {
        return "login/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm form, HttpServletRequest request, Model model) {

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null) {
            // 로그인 실패: 다시 로그인 페이지로 보내면서 에러 메시지 전달
            model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "login/login";
        }

        // 로그인 성공: 세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        // 세션에 로그인 회원 정보 보관
        session.setAttribute("loginMember", loginMember);

        return "redirect:/";
    }
}
