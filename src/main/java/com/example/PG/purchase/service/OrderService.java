package com.example.PG.purchase.service;

import com.example.PG.purchase.domain.Order;
import com.example.PG.purchase.repository.OrderRepository;
import com.example.PG.user.member.domain.Member;
import com.example.PG.user.member.repository.MemberRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final IamportClient iamportClient;

    public void validateAndCompletePayment(String impUid, String merchantUid, Long memberId) throws Exception {
        // 1. 포트원 API 조회 (외부 통신)
        IamportResponse<Payment> paymentResponse = iamportClient.paymentByImpUid(impUid);

        if (paymentResponse.getResponse() == null || !"paid".equals(paymentResponse.getResponse().getStatus())) {
            throw new RuntimeException("결제 검증 실패: 유효하지 않은 결제 상태");
        }

        // 2. 주문 정보 및 유저 정보 조회
        Order order = orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new RuntimeException("주문 정보를 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        // 3. 금액 대조 (필수 보안 사항!)
        if (paymentResponse.getResponse().getAmount().intValue() != order.getAmount()) {
            throw new RuntimeException("결제 금액 불일치 위변조 시도 감지");
        }

        // 4. 상태 변경 (원자적 처리)
        order.completePayment(impUid); // 주문 상태 PAID로
        member.setHasGame(true);       // 게임 권한 부여
    }
    public void createOrder(String merchantUid, int amount, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        Order order = Order.builder()
                .merchantUid(merchantUid)
                .amount(amount)
                .member(member)
                .build(); // status는 기본 READY

        orderRepository.save(order);
    }
}