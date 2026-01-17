CREATE TABLE IF NOT EXISTS post (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    content     TEXT NOT NULL,
    author_name VARCHAR(100) NOT NULL, -- 목록 조회 성능을 위해 이름 직접 저장
    member_id   BIGINT NOT NULL,       -- 실제 회원 테이블과의 연결 (FK)
    view_count  INT DEFAULT 0,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- 외래키 설정: 회원이 삭제되면 그 회원의 글도 삭제하거나(CASCADE), 
    -- 혹은 정책에 따라 제한할 수 있습니다.
    CONSTRAINT fk_post_member FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 테이블 코멘트
COMMENT ON TABLE post IS '게시글 정보';

-- 컬럼 코멘트
COMMENT ON COLUMN post.id IS '게시글 고유 번호';
COMMENT ON COLUMN post.title IS '게시글 제목';
COMMENT ON COLUMN post.content IS '게시글 본문';
COMMENT ON COLUMN post.author_name IS '작성자 이름 (표시용)';
COMMENT ON COLUMN post.member_id IS '작성자 고유 ID (FK)';
COMMENT ON COLUMN post.view_count IS '조회수';
COMMENT ON COLUMN post.created_at IS '작성 시각';
COMMENT ON COLUMN post.updated_at IS '수정 시각';