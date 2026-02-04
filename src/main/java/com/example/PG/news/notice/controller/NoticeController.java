package com.example.PG.news.notice.controller;

import com.example.PG.board.domain.dto.BoardWriteDto;
import com.example.PG.news.notice.domain.Notice;
import com.example.PG.news.notice.domain.dto.NoticeCacheDto;
import com.example.PG.news.notice.domain.dto.NoticeWriteDto;
import com.example.PG.news.notice.service.NoticeService;
import com.example.PG.user.member.domain.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/news/notice")
public class NoticeController {
    private final NoticeService noticeService;

    // 1. 글쓰기 폼 페이지 이동
    @GetMapping("/write")
    public String writeForm(HttpSession session, Model model) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null || !loginMember.getRole().name().equals("ADMIN")) {
            return "redirect:/news/notice";
        }
        // th:object="${noticeWriteDto}"와 연결될 빈 객체를 반드시 넘겨줘야 에러가 안 납니다!
        model.addAttribute("noticeWriteDto", new NoticeWriteDto());
        return "news/noticeWrite";
    }

    // 2. 글 저장 처리 (Service의 write 메서드 호출)
    @PostMapping("/write")
    public String write(@ModelAttribute NoticeWriteDto noticeDto,
                        HttpSession session) { // @SessionAttribute 대신 HttpSession 권장

        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null || !loginMember.getRole().name().equals("ADMIN")) {
            // 관리자가 아니면 메인으로 보냄 (시큐리티가 먼저 막겠지만 이중 방어)
            return "redirect:/";
        }

        noticeService.write(noticeDto, loginMember);
        return "redirect:/news/notice";
    }
    
    // 3. 상세 보기 (Service의 getNotices 메서드 호출) DB 조회
    /*@GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        // 서비스에서 findById를 수행하고 viewCount를 올리는 getNotices 호출
        Notice notice = noticeService.getNotices(id);
        model.addAttribute("notice", notice);
        return "news/noticeDetail";
    }*/

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        // 서비스 메서드명을 새로 만든 getNoticeDetailWithCache(또는 getNoticeDetail)로 변경
        // 반환 타입이 NoticeCacheDto이므로 에러 없이 Redis 캐싱이 작동합니다.
        NoticeCacheDto notice = noticeService.getNoticeDetailWithCache(id);
        model.addAttribute("notice", notice);
        return "news/noticeDetail"; // 파일명이 noticeDetails 인지 noticeDetail 인지 확인하세요!
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("dto") NoticeWriteDto dto,
                         HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");

        // 1. 세션 체크
        if (loginMember == null) {
            return "redirect:/login";
        }

        // 2. 서비스 호출 (내부에서 본인 확인 로직 수행)
        try {
            noticeService.update(id, dto, loginMember.getId());
        } catch (IllegalStateException e) {
            // 권한이 없을 경우 리스트로 튕겨내기
            return "redirect:/news/notice";
        }

        // 3. 수정 후 다시 상세 페이지로 이동
        return "redirect:/news/notice/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/login";
        }

        try {
            noticeService.delete(id, loginMember.getId());
        } catch (IllegalStateException e) {
            // 권한이 없을 경우 에러 메시지와 함께 리다이렉트 (필요 시 로직 추가)
            return "redirect:/news/notice";
        }

        return "redirect:/news/notice";
    }
}
