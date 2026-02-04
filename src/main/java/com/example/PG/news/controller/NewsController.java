package com.example.PG.news.controller;

import com.example.PG.news.notice.domain.Notice;
import com.example.PG.news.notice.domain.dto.NoticeCacheDto;
import com.example.PG.news.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

    private final NoticeService noticeService;

    @GetMapping("/notice")
    public String notice(Model model) {
       // List<Notice> notices = noticeService.findNotices(); // DB 조회 로직
        List<NoticeCacheDto> notices = noticeService.findNotices();

        // 2. notice.html로 데이터 전달
        model.addAttribute("notices", notices);
        return "news/notice";
    }
}
