package vn.hieu.jobhunter.service.payment;

import org.springframework.stereotype.Service;
import vn.hieu.jobhunter.domain.PaymentHistory;

@Service("MOMO")
public class MomoPaymentStrategy implements PaymentStrategy {

    @Override
    public String generatePaymentUrl(PaymentHistory paymentHistory, String returnUrl) {
        // Mock MOMO implementation
        return "https://test-payment.momo.vn/v2/gateway/api/create?amount=" +
                (long) paymentHistory.getAmount() +
                "&orderId=" + paymentHistory.getTransactionId() +
                "&returnUrl=" + returnUrl;
    }
}
