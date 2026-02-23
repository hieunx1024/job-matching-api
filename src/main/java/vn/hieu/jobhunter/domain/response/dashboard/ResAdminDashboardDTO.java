package vn.hieu.jobhunter.domain.response.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResAdminDashboardDTO {
    private long totalUsers;
    private long totalCompanies;
    private long totalJobs;
    private long totalResumes; // You can calculate this if needed. Let's say 0 for now or sum.
    private long totalSubscribedUsers;
    private double totalRevenue;
}
