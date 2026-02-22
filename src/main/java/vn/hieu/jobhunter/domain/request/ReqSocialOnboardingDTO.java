package vn.hieu.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqSocialOnboardingDTO {
    @NotBlank(message = "Role không được để trống")
    private String role;
}
