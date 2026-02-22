package vn.hieu.jobhunter.domain.response.resume;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long total;
    private long pending;
    private long reviewing;
    private long approved;
    private long rejected;
}
