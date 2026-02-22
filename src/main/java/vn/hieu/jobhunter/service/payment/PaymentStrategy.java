package vn.hieu.jobhunter.service.payment;

import vn.hieu.jobhunter.domain.PaymentHistory;

public interface PaymentStrategy {
    String generatePaymentUrl(PaymentHistory paymentHistory, String returnUrl);
}
