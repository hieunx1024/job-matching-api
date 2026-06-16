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
    private final jakarta.persistence.EntityManager entityManager;

    private long countBefore(String entityName, String dateField, java.time.Instant date) {
        return entityManager.createQuery(
                "SELECT COUNT(e) FROM " + entityName + " e WHERE e." + dateField + " <= :date", Long.class)
                .setParameter("date", date)
                .getSingleResult();
    }

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

        dto.setTotalCandidates(userRepository.countByRole_Name("CANDIDATE"));
        dto.setTotalHRs(userRepository.countByRole_Name("HR"));

        // Fetch recent payments
        java.util.List<vn.hieu.jobhunter.domain.PaymentHistory> payments = paymentHistoryRepository.findTop10ByStatusOrderByPaymentDateDesc("SUCCESS");
        java.util.List<ResAdminDashboardDTO.RecentPayment> recentPayments = payments.stream().map(p -> {
            ResAdminDashboardDTO.RecentPayment rp = new ResAdminDashboardDTO.RecentPayment();
            rp.setUserName(p.getUser() != null ? p.getUser().getName() : "N/A");
            rp.setUserEmail(p.getUser() != null ? p.getUser().getEmail() : "N/A");
            rp.setPlanName(p.getSubscription() != null ? p.getSubscription().getName() : "N/A");
            rp.setAmount(p.getAmount());
            rp.setPaymentDate(p.getPaymentDate());
            rp.setStatus(p.getStatus());
            return rp;
        }).collect(java.util.stream.Collectors.toList());
        dto.setRecentPayments(recentPayments);

        // Generate monthly time-series growth stats for the last 6 months ending at current Month
        java.time.YearMonth currentYearMonth = java.time.YearMonth.now();
        java.util.List<ResAdminDashboardDTO.TimeSeriesData> timeSeries = new java.util.ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            java.time.YearMonth ym = currentYearMonth.minusMonths(i);
            java.time.LocalDate endOfYmLocalDate = ym.atEndOfMonth();
            java.time.LocalDateTime endOfYmLocalDateTime = endOfYmLocalDate.atTime(23, 59, 59);
            java.time.Instant endOfMonthInstant = endOfYmLocalDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant();

            String monthName = "T" + ym.getMonthValue();
            
            long usersCount = countBefore("User", "createdAt", endOfMonthInstant);
            long companiesCount = countBefore("Company", "createdAt", endOfMonthInstant);
            long jobsCount = countBefore("Job", "createdAt", endOfMonthInstant);
            long resumesCount = countBefore("Resume", "createdAt", endOfMonthInstant);
            long subCount = countBefore("UserSubscription", "createdAt", endOfMonthInstant);

            ResAdminDashboardDTO.TimeSeriesData tsd = new ResAdminDashboardDTO.TimeSeriesData(
                monthName, usersCount, companiesCount, jobsCount, resumesCount, subCount
            );
            timeSeries.add(tsd);
        }
        dto.setTimeSeriesData(timeSeries);

        return ResponseEntity.ok(dto);
    }
}
