package vn.hieu.jobhunter.domain.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResCompanyDTO {
    private long id;
    private String name;
    private String description;
    private String address;
    private String logo;
    private boolean isVerified;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
