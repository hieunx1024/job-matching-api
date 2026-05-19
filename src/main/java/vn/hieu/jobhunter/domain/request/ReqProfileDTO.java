package vn.hieu.jobhunter.domain.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ReqProfileDTO {
    private String name;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;
    private MultipartFile file;
}
