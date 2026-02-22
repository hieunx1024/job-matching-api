package vn.hieu.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import vn.hieu.jobhunter.service.PaymentService;
import vn.hieu.jobhunter.util.SecurityUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPayment(
            @RequestParam String method,
            @RequestParam long subscriptionId,
            @RequestParam String returnUrl) {

        String userEmail = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Chưa đăng nhập"));

        String paymentUrl = paymentService.createPayment(method, subscriptionId, userEmail, returnUrl);
        return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<String> vnpayReturn(
            @RequestParam("vnp_TxnRef") String txnRef,
            @RequestParam("vnp_ResponseCode") String responseCode) {

        if ("00".equals(responseCode)) {
            paymentService.processPaymentWebhook(txnRef, "SUCCESS");
            return ResponseEntity.ok("Giao dịch thành công");
        } else {
            paymentService.processPaymentWebhook(txnRef, "FAILED");
            return ResponseEntity.badRequest().body("Giao dịch thất bại");
        }
    }
}
