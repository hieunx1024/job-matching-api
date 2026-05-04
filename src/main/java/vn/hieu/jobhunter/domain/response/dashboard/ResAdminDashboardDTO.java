package vn.hieu.jobhunter.domain.response.dashboard;

import java.util.List;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResAdminDashboardDTO {
    private long totalUsers;
    private long totalCompanies;
    private long totalJobs;
    private long totalResumes;
    private long totalSubscribedUsers;
    private double totalRevenue;
    private long totalCandidates;
    private long totalHRs;
    private List<RecentPayment> recentPayments;

    @Getter
    @Setter
    public static class RecentPayment {
        private String userName;
        private String userEmail;
        private String planName;
        private double amount;
        private Instant paymentDate;
        private String status;
    }
}
