package com.example.PG.board.service;

import com.example.PG.board.domain.Board;
import com.example.PG.board.domain.dto.BoardWriteDto;
import com.example.PG.board.repository.BoardRepository;
import com.example.PG.user.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;

    @Transactional
    public Long write(BoardWriteDto dto, Member member) {
        // DTO의 데이터를 꺼내서 엔티티로 변환 (빌더 사용)
        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .authorName(member.getName())
                .member(member)
                .build();

        return boardRepository.save(board).getId();
    }

    @Transactional
    public Board getPost(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        board.addViewCount(); // 엔티티 내부 메서드 활용 (더티 체킹으로 자동 업데이트)
        return board;
    }

    public List<Board> findPosts() {
        return boardRepository.findAllByOrderByIdDesc();
    }
    
    @Transactional
    public void update(Long boardId, BoardWriteDto dto, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!board.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // 엔티티 내부의 변경 메서드 활용 (더티 체킹)
        board.update(dto.getTitle(), dto.getContent());
    }

    @Transactional
    public void delete(Long boardId, Long memberId) {
        // 1. 게시글이 있는지 먼저 찾기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 중요: 게시글 작성자의 ID와 현재 로그인한 유저의 ID가 같은지 비교
        if (!board.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        // 3. 본인이 맞다면 삭제 실행
        boardRepository.delete(board);
    }
}
