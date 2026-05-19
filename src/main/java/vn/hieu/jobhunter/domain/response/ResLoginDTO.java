package vn.hieu.jobhunter.domain.response;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.hieu.jobhunter.domain.Role;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;

    private UserLogin user;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserLogin {
        private long id;
        private String email;
        private String name;
        private Role role;
        private CompanyUser company;
        private String address;
        private LocalDate dateOfBirth;
        private vn.hieu.jobhunter.util.constant.GenderEnum gender;
        private String provider;

        public UserLogin(long id, String email, String name, Role role, CompanyUser company) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.role = role;
            this.company = company;
        }

        public UserLogin(long id, String email, String name, Role role, CompanyUser company, String address, LocalDate dateOfBirth, vn.hieu.jobhunter.util.constant.GenderEnum gender, String provider) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.role = role;
            this.company = company;
            this.address = address;
            this.dateOfBirth = dateOfBirth;
            this.gender = gender;
            this.provider = provider;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUser {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserGetAccount {
        private UserLogin user;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken {
        private long id;
        private String email;
        private String name;
    }

}
