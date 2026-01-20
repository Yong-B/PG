package com.example.PG.purchase.controller;

import com.example.PG.user.member.domain.Member;
import com.example.PG.user.member.repository.MemberRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final MemberRepository memberRepository;
    private IamportClient iamportClient;

    @Value("${iamport.api-key}")
    private String apiKey;

    @Value("${iamport.api-secret}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, secretKey);
    }

    @PostMapping("/payment/validation/{imp_uid}")
    @ResponseBody
    public ResponseEntity<String> validateIamport(
            @PathVariable String imp_uid,
            @RequestBody Map<String, Object> paymentData,
            HttpSession session) {
        try {
            // 아임포트 결제 정보 조회
            IamportResponse<Payment> payment = iamportClient.paymentByImpUid(imp_uid);

            // 세션에서 로그인 유저 가져오기
            Member loginMember = (Member) session.getAttribute("loginMember");

            if (loginMember != null && "paid".equals(payment.getResponse().getStatus())) {
                // 2. 핵심: 유저 상태 변경 및 DB 저장
                loginMember.setHasGame(true);
                memberRepository.save(loginMember); // 여기서 실제 DB의 has_game이 true로 바뀜!

                // 3. 세션 정보도 최신화 (안 하면 로그아웃 전까지 화면 안 바뀜)
                session.setAttribute("loginMember", loginMember);

                log.info("유저 {} 결제 완료 및 DB 업데이트 성공", loginMember.getLoginId());
                return ResponseEntity.ok("success");
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("검증 실패");
        } catch (Exception e) {
            log.error("결제 검증 오류: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
        }
    }
}
