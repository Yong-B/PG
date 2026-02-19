package com.example.PG.news.notice.service;

import com.example.PG.news.notice.domain.Notice;
import com.example.PG.news.notice.domain.dto.NoticeCacheDto;
import com.example.PG.news.notice.domain.dto.NoticeWriteDto;
import com.example.PG.news.notice.repository.NoticeRepository;
import com.example.PG.user.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {


    /*private final NoticeRepository noticeRepository;

    @Transactional
    public Long write(NoticeWriteDto dto, Member member) {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type(dto.getType())
                .member(member)
                .build();

        return noticeRepository.save(notice).getId();
    }
    
    @Transactional
    public Notice getNotices(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        
        notice.addViewCount();
        return notice;
    }
    
    public List<Notice> findNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }
    
    @Transactional
    public void update(Long noticeId, NoticeWriteDto dto, Long memberId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!notice.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // 엔티티 내부의 변경 메서드 활용 (더티 체킹)
        notice.update(dto.getTitle(), dto.getContent());
    }

    @Transactional
    public void delete(Long noticeId, Long memberId) {
        // 1. 게시글이 있는지 먼저 찾기
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 중요: 게시글 작성자의 ID와 현재 로그인한 유저의 ID가 같은지 비교
        if (!notice.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        // 3. 본인이 맞다면 삭제 실행
        noticeRepository.delete(notice);
    }*/

    private final NoticeRepository noticeRepository;

    @Transactional
    public Long write(NoticeWriteDto dto, Member member) {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .type(dto.getType())
                .member(member)
                .build();

        return noticeRepository.save(notice).getId();
    }

    // 1. 기존에 getCachedNoticeDto에 있던 캐시 설정을 이쪽(정문)으로 옮깁니다.
    @Transactional
    @Cacheable(value = "noticeDetail", key = "#id")
    public NoticeCacheDto getNoticeDetailWithCache(Long id) {

        // 캐시가 없을 때만 아래 로직이 실행됩니다.
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        notice.addViewCount(); // 조회수 증가

        // 2. 변환 로직은 메서드를 호출하지 않고 바로 처리하거나, 단순 메서드로 호출합니다.
        return NoticeCacheDto.fromEntity(notice);
    }

    // 리스트 조회도 캐싱 적용 가능
    @Cacheable(value = "noticeList")
    public List<NoticeCacheDto> findNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(NoticeCacheDto::fromEntity)
                .toList();
    }
    
    @Transactional
    public void update(Long noticeId, NoticeWriteDto dto, Long memberId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if (!notice.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // 엔티티 내부의 변경 메서드 활용 (더티 체킹)
        notice.update(dto.getTitle(), dto.getContent());
    }

    @Transactional
    public void delete(Long noticeId, Long memberId) {
        // 1. 게시글이 있는지 먼저 찾기
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 2. 중요: 게시글 작성자의 ID와 현재 로그인한 유저의 ID가 같은지 비교
        if (!notice.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        // 3. 본인이 맞다면 삭제 실행
        noticeRepository.delete(notice);
    }

}
