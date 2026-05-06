package vn.hieu.jobhunter.service;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.hieu.jobhunter.repository.JobRepository;

@Service
public class JobExpiryScheduler {

    private final Logger log = LoggerFactory.getLogger(JobExpiryScheduler.class);
    private final JobRepository jobRepository;

    public JobExpiryScheduler(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * Tự động quét và đóng các bài tuyển dụng đã hết hạn (endDate < hiện tại)
     * Chạy định kỳ vào lúc 00:00:00 hằng ngày.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deactivateExpiredJobs() {
        log.info(">>> BẮT ĐẦU QUÉT BÀI ĐĂNG TUYỂN DỤNG HẾT HẠN...");
        Instant now = Instant.now();
        int updatedCount = this.jobRepository.deactivateExpiredJobs(now);
        log.info(">>> QUÉT HOÀN TẤT. Đã tự động đóng/ẩn {} tin tuyển dụng hết hạn (endDate < {}).", updatedCount, now);
    }
}
