package com.example.PG.purchase.controller;
import com.example.PG.purchase.service.OrderService;
import com.example.PG.user.member.domain.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final OrderService orderService;

    @PostMapping("/payment/validation/{imp_uid}")
    @ResponseBody
    public ResponseEntity<String> validateIamport(
            @PathVariable String imp_uid,
            @RequestBody Map<String, String> paymentData,
            HttpSession session) {

        Member loginMember = (Member) session.getAttribute("loginMember");
        String merchantUid = paymentData.get("merchant_uid");

        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }

        try {
            // 서비스 호출
            orderService.validateAndCompletePayment(imp_uid, merchantUid, loginMember.getId());

            // 세션 갱신 (화면 반영용)
            loginMember.setHasGame(true);
            session.setAttribute("loginMember", loginMember);

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @PostMapping("/payment/prepare")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> preparePayment(HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // 서버에서 가격 결정 (하드코딩을 하더라도 서버 코드에 있어야 안전함)
        int fixedPrice = 30000;
        String merchantUid = "wanderin_" + System.currentTimeMillis();

        // DB에 READY 상태로 주문 저장
        orderService.createOrder(merchantUid, fixedPrice, loginMember.getId());

        // 클라이언트에 주문 정보 전달
        Map<String, Object> response = new HashMap<>();
        response.put("merchant_uid", merchantUid);
        response.put("amount", fixedPrice);

        return ResponseEntity.ok(response);
    }
}