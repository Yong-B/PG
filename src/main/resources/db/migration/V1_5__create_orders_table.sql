CREATE TABLE IF NOT EXISTS orders(
    id              BIGSERIAL PRIMARY KEY,
    merchant_uid    VARCHAR(100) UNIQUE NOT NULL, -- 우리 서버 주문번호 (ex: ord_20260219_01)
    imp_uid         VARCHAR(100),                -- 포트원 결제 고유 번호 (결제 완료 후 채워짐)
    member_id       BIGINT NOT NULL,             -- 결제한 유저 식별자
    amount          INT NOT NULL,                -- 결제 금액
    status          VARCHAR(20) NOT NULL,        -- 상태 (READY: 결제 대기, PAID: 결제 완료, CANCEL: 취소)
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 외래키 설정: member 테이블의 id와 연결
    CONSTRAINT fk_member_order FOREIGN KEY (member_id) REFERENCES member(id)
);

-- 인덱스 추가 (조회 성능 향상)
CREATE INDEX idx_orders_merchant_uid ON orders(merchant_uid);
CREATE INDEX idx_orders_member_id ON orders(member_id);

-- 코멘트 추가
COMMENT ON COLUMN orders.merchant_uid IS '자체 생성 주문번호';
COMMENT ON COLUMN orders.imp_uid IS '포트원 거래 고유번호';
COMMENT ON COLUMN orders.status IS '결제 상태 (READY, PAID, CANCEL)';