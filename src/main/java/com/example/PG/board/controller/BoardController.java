package com.example.PG.board.controller;


import com.example.PG.board.domain.Board;
import com.example.PG.board.domain.dto.BoardWriteDto;
import com.example.PG.board.service.BoardService;
import com.example.PG.user.member.domain.Member;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 게시판 조회
    @GetMapping
    public String list(Model model) {
        List<Board> posts = boardService.findPosts();
        model.addAttribute("posts", posts);
        return "board/boardList";
    }

    // 게시판 글쓰기 페이지
    @GetMapping("/write")
    public String writeForm(Model model, HttpSession session) {
        if (session.getAttribute("loginMember") == null) {
            return "redirect:/login";
        }
        model.addAttribute("boardWriteDto", new BoardWriteDto());
        return "board/boardWrite";
    }

    // 게시글 저장
    @PostMapping("/write")
    public String write(@Valid @ModelAttribute("boardWriteDto") BoardWriteDto dto,
                        BindingResult bindingResult,
                        HttpSession session,
                        Model model) {

        // 1. DTO 검증 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            // 에러 정보를 담은 채로 다시 글쓰기 폼으로 이동
            return "board/boardWrite";
        }

        // 2. 세션 체크 (기존 로직)
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) {
            return "redirect:/login";
        }

        boardService.write(dto, loginMember);
        return "redirect:/board";
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Board post = boardService.getPost(id);
        model.addAttribute("post", post);

        // (선택 사항) 만약 폼 바인딩을 위해 DTO가 필요하다면 빈 객체를 넣어줄 수 있습니다.
        // 하지만 우리가 만든 자바스크립트 방식은 name="title" 속성으로 바로 전송하므로 생략 가능합니다.
        return "board/boardDetail";
    }

    // 2. 수정 실행 (저장 완료 버튼 클릭 시 호출)
    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("dto") BoardWriteDto dto,
                         HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");

        // 1. 세션 체크
        if (loginMember == null) {
            return "redirect:/login";
        }

        // 2. 서비스 호출 (내부에서 본인 확인 로직 수행)
        try {
            boardService.update(id, dto, loginMember.getId());
        } catch (IllegalStateException e) {
            // 권한이 없을 경우 리스트로 튕겨내기
            return "redirect:/board";
        }

        // 3. 수정 후 다시 상세 페이지로 이동
        return "redirect:/board/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");

        if (loginMember == null) {
            return "redirect:/login";
        }

        try {
            boardService.delete(id, loginMember.getId());
        } catch (IllegalStateException e) {
            // 권한이 없을 경우 에러 메시지와 함께 리다이렉트 (필요 시 로직 추가)
            return "redirect:/board";
        }

        return "redirect:/board";
    }
}
