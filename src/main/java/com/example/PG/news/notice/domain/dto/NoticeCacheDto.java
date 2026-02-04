package com.example.PG.news.notice.domain.dto;

import com.example.PG.news.notice.domain.Notice;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeCacheDto implements Serializable {
    private Long id;
    private String title;
    private String content;
    private String writer; // Member 엔티티 대신 이름만 저장
    private String type;
    private Integer viewCount;
    private LocalDateTime createdAt;

    public static NoticeCacheDto fromEntity(Notice notice) {
        return NoticeCacheDto.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .writer(notice.getMember().getName()) // Proxy 문제 해결 포인트
                .type(notice.getType().name())
                .viewCount(notice.getViewCount())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}