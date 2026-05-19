package vn.hieu.jobhunter.domain.request;

import java.time.LocalDate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    private LocalDate dateOfBirth;

    @NotNull(message = "Giới tính không được để trống")
    private GenderEnum gender;

    @NotBlank(message = "Vui lòng nhập Tỉnh/Thành phố")
    private String address;

    @NotBlank(message = "Vai trò không được để trống")
    // Chỉ chấp nhận: "CANDIDATE" hoặc "RECRUITER" (dữ liệu từ FE gửi lên)
    private String role;
}
