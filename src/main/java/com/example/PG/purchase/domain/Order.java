package com.example.PG.purchase.domain;

import com.example.PG.user.member.domain.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders") // order는 예약어인 경우가 많아 orders 권장
@Getter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String merchantUid;

    private String impUid;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private String status; // 초기값은 Builder에서 READY로 설정됨

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 생성 시간 추적 (Audit) - 상속을 쓰거나 직접 추가
    private LocalDateTime createdAt;

    @Builder
    public Order(String merchantUid, int amount, Member member) {
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.member = member;
        this.status = "READY";
        this.createdAt = LocalDateTime.now();
    }

    // 비즈니스 메서드: 결제 완료 처리
    public void completePayment(String impUid) {
        this.impUid = impUid;
        this.status = "PAID";
    }

    // 비즈니스 메서드: 결제 실패/취소 처리 (필요시 추가)
    public void failPayment() {
        this.status = "FAILED";
    }
}