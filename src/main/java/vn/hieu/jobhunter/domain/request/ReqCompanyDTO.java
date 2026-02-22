package vn.hieu.jobhunter.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqCompanyDTO {
    @NotBlank(message = "Tên công ty không được để trống")
    private String name;

    private String description;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    private String logo;
}
