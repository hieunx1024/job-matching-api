package vn.hieu.jobhunter.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.UserSubscription;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    // Tìm gói đăng ký còn hạn (Active=true, EndDate > now)
    // Sắp xếp theo ngày kết thúc để ưu tiên gói sắp hết hạn trước (nếu muốn)
    List<UserSubscription> findByUserAndActiveTrueAndEndDateAfter(User user, Instant now);

    Optional<UserSubscription> findFirstByUserAndActiveTrueAndEndDateAfterOrderByEndDateAsc(User user, Instant now);

    List<UserSubscription> findByUserAndActiveTrue(User user);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT us.user.id) FROM UserSubscription us WHERE us.active = true")
    long countDistinctUserByActiveTrue();
}
