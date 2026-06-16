package vn.hieu.jobhunter.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
// Use appropriate Google API package
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import vn.hieu.jobhunter.domain.Company;
import vn.hieu.jobhunter.domain.Role;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.response.ResCreateUserDTO;
import vn.hieu.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hieu.jobhunter.domain.response.ResUserDTO;
import vn.hieu.jobhunter.domain.response.ResultPaginationDTO;
import vn.hieu.jobhunter.domain.request.ChangePasswordRequestDTO;
import vn.hieu.jobhunter.repository.UserRepository;
import vn.hieu.jobhunter.util.SecurityUtil;
import vn.hieu.jobhunter.util.error.IdInvalidException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final JavaMailSender javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            CompanyService companyService,
            RoleService roleService,
            JavaMailSender javaMailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
        this.javaMailSender = javaMailSender;
        this.passwordEncoder = passwordEncoder;
    }

    // =================== CREATE USER ===================
    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.orElse(null));
        }

        // check role
        if (user.getRole() != null) {
            Role r = this.roleService.fetchById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }

        return this.userRepository.save(user);
    }

    // =================== REGISTER WITH ROLE ===================
    public User handleRegister(vn.hieu.jobhunter.domain.request.ReqRegisterDTO dto) throws IdInvalidException {
        // Validate Role (only CANDIDATE or RECRUITER allowed)
        if (!dto.getRole().equalsIgnoreCase("CANDIDATE") && !dto.getRole().equalsIgnoreCase("RECRUITER")) {
            throw new IdInvalidException("Invalid role. Only CANDIDATE or RECRUITER are allowed.");
        }

        Role role = this.roleService.fetchByName(dto.getRole());
        if (role == null) {
            // Fallback: If role is RECRUITER but not found, try HR
            if ("RECRUITER".equalsIgnoreCase(dto.getRole())) {
                role = this.roleService.fetchByName("HR");
            }
        }

        if (role == null) {
            throw new IdInvalidException("Role does not exist: " + dto.getRole());
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setGender(dto.getGender());
        user.setAddress(dto.getAddress());
        user.setRole(role);

        return this.userRepository.save(user);
    }

    // =================== DELETE ===================
    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        rs.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent()
                .stream()
                .map(this::convertToResUserDTO)
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;
    }

    // =================== UPDATE USER ===================
    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setGender(reqUser.getGender());
            currentUser.setDateOfBirth(reqUser.getDateOfBirth());
            currentUser.setName(reqUser.getName());

            if (reqUser.getCompany() != null) {
                Optional<Company> companyOptional = this.companyService.findById(reqUser.getCompany().getId());
                currentUser.setCompany(companyOptional.orElse(null));
            }

            if (reqUser.getRole() != null) {
                Role r = this.roleService.fetchById(reqUser.getRole().getId());
                currentUser.setRole(r != null ? r : null);
            }

            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    // =================== FETCH BY USERNAME ===================
    public User handleGetUserByUsername(String username) {
        // Use eager fetch to load role and permissions for JWT token generation
        return this.userRepository.findByEmailWithRoleAndPermissions(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public Role getRoleById(long id) {
        return this.roleService.fetchById(id);
    }

    // =================== CONVERT DTO ===================
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser com = new ResCreateUserDTO.CompanyUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setDateOfBirth(user.getDateOfBirth());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }
        return res;
    }

    public ResultPaginationDTO fetchAllUserByCreatorOrSelf(String creator, long userId, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAllByCreatorOrSelf(creator, userId, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        rs.setMeta(mt);

        List<ResUserDTO> listUser = pageUser.getContent()
                .stream()
                .map(this::convertToResUserDTO)
                .collect(Collectors.toList());

        rs.setResult(listUser);
        return rs;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }

        res.setId(user.getId());
        res.setName(user.getName());
        res.setDateOfBirth(user.getDateOfBirth());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser com = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());
            res.setCompany(com);
        }

        if (user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setDateOfBirth(user.getDateOfBirth());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setStatus(user.getStatus());
        return res;
    }

    // =================== REFRESH TOKEN ===================
    public void updateUserToken(String token, String email) {
        this.userRepository.updateRefreshToken(token, email);
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public User findOrCreateGoogleUser(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        User user = this.handleGetUserByUsername(email);

        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setProvider("GOOGLE");
            user.setStatus("ACTIVE"); // Google users are verified automatically

            // Set dummy password for Google users; they never use this to login
            user.setPassword(passwordEncoder.encode("GOOGLE_LOGIN_DUMMY_PASSWORD_123"));

            user = this.handleCreateUser(user);
        }

        return user;
    }

    // =================== EMAIL VERIFICATION ===================
    public String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);
        return token;
    }

    public void sendVerificationEmail(User user, String token) {
        String subject = "Account Verification - JobHunter";
        String verificationUrl = "http://localhost:8080/api/v1/auth/verify?token=" + token;
        String message = "Hello " + user.getName() + ",\n\n" +
                "Please click the link below to verify your account:\n" + verificationUrl;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }

    public User getUserByVerificationToken(String token) {
        return userRepository.findByVerificationToken(token);
    }

    public void activateUser(User user) {
        user.setStatus("ACTIVE");
        user.setVerificationToken(null);
        userRepository.save(user);
    }

    public User updateUserStatus(long id, String status) {
        User user = this.fetchUserById(id);
        if (user != null) {
            user.setStatus(status);
            return userRepository.save(user);
        }
        return null;
    }

    // =================== PASSWORD RESET (FORGOT PASSWORD) ===================
    public void handleForgotPassword(String email) throws IdInvalidException {
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new IdInvalidException("Email không tồn tại trong hệ thống");
        }

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(Instant.now().plusSeconds(900)); // Hết hạn sau 15 phút (900 giây)
        userRepository.save(user);

        // Gửi email khôi phục mật khẩu
        String subject = "Khôi phục mật khẩu - JobHunter";
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;
        String message = "Xin chào " + user.getName() + ",\n\n" +
                "Bạn đã yêu cầu khôi phục mật khẩu trên hệ thống JobHunter.\n" +
                "Vui lòng click vào link bên dưới để thiết lập mật khẩu mới (Link có hiệu lực trong 15 phút):\n" + resetUrl + "\n\n" +
                "Nếu bạn không yêu cầu hành động này, vui lòng bỏ qua email này.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        javaMailSender.send(mailMessage);
    }

    public void handleResetPassword(String token, String newPassword) throws IdInvalidException {
        User user = userRepository.findByPasswordResetToken(token);
        if (user == null) {
            throw new IdInvalidException("Mã xác thực không hợp lệ");
        }

        if (user.getPasswordResetTokenExpiry().isBefore(Instant.now())) {
            throw new IdInvalidException("Yêu cầu khôi phục mật khẩu đã hết hạn. Vui lòng thử lại");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
    }

    // =================== ROLE SELECTION ===================
    public User handleSelectRole(String email, String roleName) throws IdInvalidException {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser == null) {
            throw new IdInvalidException("User not found");
        }

        // Check if role is already selected (Assume they start with null or "USER")
        // If current role is one of the final roles, prevent change.
        if (currentUser.getRole() != null) {
            String currentRole = currentUser.getRole().getName();
            // Assuming "USER" is the default temporary role.
            // If it is NOT "USER" and NOT null, it means they already selected.
            if (!"USER".equalsIgnoreCase(currentRole)) {
                throw new IdInvalidException("User role already selected (" + currentRole + ")");
            }
        }

        Role role = this.roleService.fetchByName(roleName);
        if (role == null) {
            // Fallback: If role is RECRUITER but not found, try HR
            if ("RECRUITER".equalsIgnoreCase(roleName)) {
                role = this.roleService.fetchByName("HR");
            }
        }

        if (role == null) {
            throw new IdInvalidException("Role " + roleName + " not found");
        }

        currentUser.setRole(role);
        return this.userRepository.save(currentUser);
    }

    // =================== CHANGE PASSWORD ===================
    public void handleChangePassword(ChangePasswordRequestDTO dto) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (email.isEmpty()) {
            throw new IdInvalidException("Access token is invalid or expired");
        }

        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser == null) {
            throw new IdInvalidException("User does not exist");
        }

        // 1. Check if current password matches
        if (!this.passwordEncoder.matches(dto.getCurrentPassword(), currentUser.getPassword())) {
            throw new IdInvalidException("Current password is incorrect");
        }

        // 2. Check if new password matches confirm password
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IdInvalidException("New password and confirm password do not match");
        }

        // 3. Optional: Check if new password is same as old password
        if (this.passwordEncoder.matches(dto.getNewPassword(), currentUser.getPassword())) {
            throw new IdInvalidException("New password cannot be the same as the old password");
        }

        // 4. Update password and security metadata
        currentUser.setPassword(this.passwordEncoder.encode(dto.getNewPassword()));
        currentUser.setPasswordLastChangedAt(Instant.now());
        currentUser.setRefreshToken(null); // Invalidate refresh token
        this.userRepository.save(currentUser);
    }
}
