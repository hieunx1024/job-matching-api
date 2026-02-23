package vn.hieu.jobhunter.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.UserSubscription;
import vn.hieu.jobhunter.repository.JobRepository;
import vn.hieu.jobhunter.repository.UserRepository;
import vn.hieu.jobhunter.repository.UserSubscriptionRepository;
import vn.hieu.jobhunter.util.error.IdInvalidException;
import vn.hieu.jobhunter.util.error.PostLimitExceededException;

@Service
public class JobPostingService {

    private final UserRepository userRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final JobRepository jobRepository;

    public JobPostingService(UserRepository userRepository,
            UserSubscriptionRepository userSubscriptionRepository,
            JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
        this.jobRepository = jobRepository;
    }

    /**
     * Logic: Kiểm tra quyền đăng tin của HR
     * Trả về true nếu thành công và trừ lượt/credit
     * Ném Exception nếu không đủ điều kiện
     */
    @Transactional
    public void validateAndConsumeJobPost(String email) throws IdInvalidException, PostLimitExceededException {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new IdInvalidException("User không tồn tại.");
        }

        // Logic 0: Free Tier (Max 2 posts per company)
        if (user.getCompany() != null) {
            long currentJobs = this.jobRepository.countByCompany(user.getCompany());
            if (currentJobs < 3) {
                // Free tier, allow post
                return;
            }
        }

        // Logic 1: Kiểm tra Gói đăng ký (Subscription)
        // Lấy gói đăng ký đầu tiên còn hiệu lực và còn hạn sử dụng
        Optional<UserSubscription> activeSub = this.userSubscriptionRepository
                .findFirstByUserAndActiveTrueAndEndDateAfterOrderByEndDateAsc(user, Instant.now());

        if (activeSub.isPresent()) {
            UserSubscription sub = activeSub.get();
            // Nếu vô hạn (-1) hoặc còn lượt (>0)
            if (sub.getSubscription().getLimitPosts() == -1 || sub.getTotalPosts() - sub.getUsedPosts() > 0) {
                // Trừ lượt nếu có giới hạn
                if (sub.getSubscription().getLimitPosts() != -1) {
                    sub.setUsedPosts(sub.getUsedPosts() + 1);
                    sub.setRemainingPosts(sub.getRemainingPosts() - 1);
                    // Nếu hết lượt thì có thể set active = false, nhưng tùy business logic
                    // Ở đây chỉ trừ lượt
                    this.userSubscriptionRepository.save(sub);
                }
                // Hợp lệ -> return
                return;
            }
        }

        // Logic 2: Kiểm tra Pay-per-post Credit
        if (user.getJobPostingCredits() > 0) {
            user.setJobPostingCredits(user.getJobPostingCredits() - 1);
            this.userRepository.save(user); // Lưu user đã cập nhật
            return;
        }

        // Nếu không thỏa mãn cả 2 -> Throw exception
        throw new PostLimitExceededException(
                "Bạn đã hết lượt đăng tin miễn phí (2/2). Vui lòng nâng cấp gói dịch vụ để tiếp tục.");
    }
}
