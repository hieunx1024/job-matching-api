package vn.hieu.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hieu.jobhunter.domain.PaymentHistory;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    Optional<PaymentHistory> findByTransactionId(String transactionId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentHistory p WHERE p.status = :status")
    double sumAmountByStatus(String status);
}
