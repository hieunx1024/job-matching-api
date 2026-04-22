package vn.hieu.jobhunter.domain.response.user;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import vn.hieu.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResProfileDTO {
    private long id;
    private String email;
    private String name;
    private String address;
    private int age;
    private GenderEnum gender;
    private List<UserCvDTO> cvs;

    @Getter
    @Setter
    public static class UserCvDTO {
        private long id;
        private String fileName;
        private String url;
        private boolean isDefault;
    }
}
