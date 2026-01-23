package com.example.PG.support.faq.controller;

import com.example.PG.support.question.domain.QuestionEntity;
import com.example.PG.support.question.service.QuestionService;
import com.example.PG.user.member.domain.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/support/faq") // URL 구조를 고객지원 하위로 맞추는 것이 관리하기 편합니다.
public class FaqController {

    private final QuestionService questionService;

    @GetMapping // ✅ 이제 /support/faq 주소 하나로 모든 데이터를 처리합니다.
    public String supportMain(Model model, HttpSession session) {
        // 1. FAQ 데이터 (하드코딩)
        List<Map<String, String>> faqs = new ArrayList<>();
        faqs.add(Map.of("question", "게임은 어디서 다운로드할 수 있나요?", "answer", "로그인 후 메인 페이지의..."));
        faqs.add(Map.of("question", "결제 가능한 수단은 무엇인가요?", "answer", "현재 카카오페이..."));
        faqs.add(Map.of("question", "권장 사양은 어떻게 되나요?", "answer", "Wanderin' Free는..."));
        faqs.add(Map.of("question", "직접 문의를 남기고 싶습니다.", "answer", "해결되지 않은 문제는..."));

        model.addAttribute("faqs", faqs);

        // 2. 세션에서 로그인한 회원 정보 가져오기
        Member loginMember = (Member) session.getAttribute("loginMember");

        // 콘솔 확인용 로그
        System.out.println("로그인 멤버 확인: " + loginMember);

        if (loginMember != null) {
            // 로그인 상태라면 문의 내역 조회
            List<QuestionEntity> myQuestions = questionService.findMyQuestions(loginMember);
            System.out.println("조회된 문의 개수: " + myQuestions.size());
            model.addAttribute("myQuestions", myQuestions);
        } else {
            // 로그인 안 했을 때 null 방지를 위해 빈 리스트 담기
            model.addAttribute("myQuestions", new ArrayList<>());
        }

        return "support/faq";
    }
}