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
     * Validate HR job posting permissions.
     * Consumes one post credit if allowed; throws an exception otherwise.
     */
    @Transactional
    public void validateAndConsumeJobPost(String email) throws IdInvalidException, PostLimitExceededException {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new IdInvalidException("User not found");
        }

        // Logic 0: Free Tier (Max 2 posts per company)
        if (user.getCompany() != null) {
            long currentJobs = this.jobRepository.countByCompany(user.getCompany());
            if (currentJobs < 3) {
                // Free tier, allow post
                return;
            }
        }

        // Subscription check
        // Fetch the first active subscription that hasn't expired
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

        // Pay-per-post Credit check
        if (user.getJobPostingCredits() > 0) {
            user.setJobPostingCredits(user.getJobPostingCredits() - 1);
            this.userRepository.save(user); // Lưu user đã cập nhật
            return;
        }

        // If no criteria met, throw exception
        throw new PostLimitExceededException(
                "Free post limit reached (2/2). Please upgrade your plan to continue.");
    }
}
