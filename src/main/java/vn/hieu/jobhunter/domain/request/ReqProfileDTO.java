package vn.hieu.jobhunter.domain.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ReqProfileDTO {
    private String name;
    private String address;
    private Integer age;
    private String gender;
    private MultipartFile file;
}
