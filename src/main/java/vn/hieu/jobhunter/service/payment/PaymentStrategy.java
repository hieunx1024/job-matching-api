package vn.hieu.jobhunter.service.payment;

import java.util.Map;
import vn.hieu.jobhunter.domain.PaymentHistory;

public interface PaymentStrategy {
    String generatePaymentUrl(PaymentHistory paymentHistory, String returnUrl);

    default boolean verifySignature(Map<String, String> params) {
        return true;
    }
}

