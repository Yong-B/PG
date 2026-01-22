package com.example.PG.support.controller;

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

    @GetMapping
    public String faqList(Model model) {
        List<Map<String, String>> faqs = new ArrayList<>();
        faqs.add(Map.of(
                "question", "게임은 어디서 다운로드할 수 있나요?",
                "answer", "로그인 후 메인 페이지의 <span class='text-blue-600 font-bold'>게임실행</span> 버튼을 클릭하시거나,<br>" +
                        "상단의 <span class='text-blue-600 font-bold'>다운로드</span> 탭에서 최신 버전의 설치 파일을 받으실 수 있습니다."
        ));
        faqs.add(Map.of(
                "question", "결제 가능한 수단은 무엇인가요?",
                "answer", "현재 <span class='font-bold text-gray-900'>카카오페이, KG이니시스, KICC</span>를 통한 결제를 지원합니다.<br>" +
                        "안전한 결제를 위해 포트원(아임포트) 시스템을 사용하고 있습니다."
        ));
        faqs.add(Map.of(
                "question", "권장 사양은 어떻게 되나요?",
                "answer", "Wanderin' Free는 유니티 엔진 기반으로 제작되었습니다.<br>" +
                        "<span class='font-bold'>Windows 10 이상, RAM 8GB, 외장 그래픽 카드</span> 환경에서 가장 쾌적하게 플레이하실 수 있습니다."
        ));
        faqs.add(Map.of(
                "question", "직접 문의를 남기고 싶습니다.",
                "answer", "해결되지 않은 문제는 <span class='font-bold text-blue-600'>'문의하기'</span> 탭을 통해 상세 내용을 남겨주세요.<br>" +
                        "개발팀에서 확인 후 신속하게 답변 드리겠습니다."
        ));

        model.addAttribute("faqs", faqs);
        return "support/faq";
    }
}