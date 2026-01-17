package com.example.PG.board.domain;

import com.example.PG.user.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "view_count")
    private int viewCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Board(String title, String content, String authorName, Member member) {
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.member = member;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 2. 비즈니스 로직에 맞는 변경 메서드 (수정 기능 등)
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    // ✅ 3. 조회수 증가 같은 특수 목적 메서드
    public void addViewCount() {
        this.viewCount++;
    }
}
