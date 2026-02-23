package vn.hieu.jobhunter.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.hieu.jobhunter.domain.PaymentHistory;
import vn.hieu.jobhunter.domain.Subscription;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.UserSubscription;
import vn.hieu.jobhunter.repository.PaymentHistoryRepository;
import vn.hieu.jobhunter.repository.SubscriptionRepository;
import vn.hieu.jobhunter.repository.UserRepository;
import vn.hieu.jobhunter.repository.UserSubscriptionRepository;
import vn.hieu.jobhunter.service.payment.PaymentStrategy;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final Map<String, PaymentStrategy> paymentStrategies;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public String createPayment(String method, long subscriptionId, String userEmail, String returnUrl) {
        // Find user & package
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription Package not found"));

        // Strategy Selection
        PaymentStrategy strategy = paymentStrategies.get(method.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }

        // Create Payment History
        PaymentHistory history = new PaymentHistory();
        history.setUser(user);
        history.setSubscription(sub);
        history.setAmount(sub.getPrice());
        history.setTransactionId(UUID.randomUUID().toString());
        history.setPaymentMethod(method.toUpperCase());
        history.setStatus("PENDING");

        paymentHistoryRepository.save(history);

        return strategy.generatePaymentUrl(history, returnUrl);
    }

    @org.springframework.transaction.annotation.Transactional
    public void processPaymentWebhook(String transactionId, String status) {
        PaymentHistory history = paymentHistoryRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (!"PENDING".equalsIgnoreCase(history.getStatus())) {
            return; // Already processed
        }

        history.setStatus(status);
        paymentHistoryRepository.save(history);

        if ("SUCCESS".equalsIgnoreCase(status)) {
            // Upgrade User Subscription
            Subscription sub = history.getSubscription();
            User user = history.getUser();

            // Logic to handle Upgrade vs New vs Auto-expire
            UserSubscription userSub = new UserSubscription();
            userSub.setUser(user);
            userSub.setSubscription(sub);
            userSub.setStartDate(Instant.now());
            userSub.setEndDate(Instant.now().plusSeconds((long) sub.getDurationDays() * 24 * 60 * 60));
            userSub.setTotalPosts(sub.getLimitPosts());
            userSub.setUsedPosts(0);
            userSub.setRemainingPosts(sub.getLimitPosts());
            userSub.setActive(true);

            // If they had an active package, we might update it to inactive, or stack the
            // posts.
            // Simplified logic: Just mark older as inactive
            userSubscriptionRepository.findByUserAndActiveTrue(user)
                    .forEach(oldSub -> {
                        oldSub.setActive(false);
                        userSubscriptionRepository.save(oldSub);
                    });

            userSubscriptionRepository.save(userSub);
        }
    }
}
