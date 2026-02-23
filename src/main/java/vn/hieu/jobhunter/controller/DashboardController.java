package vn.hieu.jobhunter.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.hieu.jobhunter.repository.CompanyRepository;
import vn.hieu.jobhunter.repository.JobRepository;
import vn.hieu.jobhunter.repository.PaymentHistoryRepository;
import vn.hieu.jobhunter.repository.UserRepository;
import vn.hieu.jobhunter.repository.UserSubscriptionRepository;
import vn.hieu.jobhunter.domain.response.dashboard.ResAdminDashboardDTO;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final vn.hieu.jobhunter.repository.ResumeRepository resumeRepository;

    @GetMapping("/admin")
    public ResponseEntity<ResAdminDashboardDTO> getAdminDashboardStats()
            throws vn.hieu.jobhunter.util.error.PermissionException {
        String email = vn.hieu.jobhunter.util.SecurityUtil.getCurrentUserLogin().orElse("");
        vn.hieu.jobhunter.domain.User user = userRepository.findByEmail(email);

        if (user == null || user.getRole() == null || !"SUPER_ADMIN".equals(user.getRole().getName())) {
            throw new vn.hieu.jobhunter.util.error.PermissionException("Bạn không có quyền truy cập dashboard admin.");
        }

        ResAdminDashboardDTO dto = new ResAdminDashboardDTO();
        dto.setTotalUsers(userRepository.count());
        dto.setTotalCompanies(companyRepository.count());
        dto.setTotalJobs(jobRepository.count());
        dto.setTotalResumes(resumeRepository.count());

        long countSubscribedUsers = userSubscriptionRepository.countDistinctUserByActiveTrue();
        dto.setTotalSubscribedUsers(countSubscribedUsers);

        double revenue = paymentHistoryRepository.sumAmountByStatus("SUCCESS");
        dto.setTotalRevenue(revenue);

        return ResponseEntity.ok(dto);
    }
}
