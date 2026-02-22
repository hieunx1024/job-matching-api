package vn.hieu.jobhunter.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.hieu.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ReqRegisterDTO {
    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;

    @Min(value = 18, message = "Tuổi phải lớn hơn hoặc bằng 18")
    private int age;

    @NotNull(message = "Giới tính không được để trống")
    private GenderEnum gender;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotBlank(message = "Vai trò không được để trống")
    // Chỉ chấp nhận: "CANDIDATE" hoặc "RECRUITER" (dữ liệu từ FE gửi lên)
    private String role;
}
