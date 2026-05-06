
package vn.hieu.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.request.GoogleLoginRequest;
import vn.hieu.jobhunter.domain.request.ReqLoginDTO;
import vn.hieu.jobhunter.domain.request.ChangePasswordRequestDTO;
import vn.hieu.jobhunter.domain.response.ResCreateUserDTO;
import vn.hieu.jobhunter.domain.response.ResLoginDTO;
import vn.hieu.jobhunter.service.GoogleAuthService;
import vn.hieu.jobhunter.service.UserService;
import vn.hieu.jobhunter.util.SecurityUtil;
import vn.hieu.jobhunter.util.annotation.ApiMessage;
import vn.hieu.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final GoogleAuthService googleAuthService;

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService,
            PasswordEncoder passwordEncoder,
            GoogleAuthService googleAuthService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.googleAuthService = googleAuthService;
    }

    // =================== LOGIN USERNAME/PASSWORD ===================
    @PostMapping("/auth/login")
    @ApiMessage("Login with Username/Password")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDto) {
        // 1. Authenticate and update Security Context
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Fetch user from DB
        User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());

        // 3. Generate token and return response
        return buildLoginResponse(currentUserDB);
    }

    // =================== LOGIN GOOGLE ===================
    @PostMapping("/auth/google")
    @ApiMessage("Login with Google")
    public ResponseEntity<ResLoginDTO> loginGoogle(@RequestBody GoogleLoginRequest req) {
        // 1. Verify Google Token
        var payload = googleAuthService.verify(req.getIdToken());

        // 2. Find or create user from Payload
        User user = userService.findOrCreateGoogleUser(payload);

        // 3. Generate token and return response
        return buildLoginResponse(user);
    }

    // =================== SOCIAL ONBOARDING (UPDATE ROLE) ===================
    @PostMapping("/auth/social-onboarding")
    @ApiMessage("Update role for social login user")
    public ResponseEntity<ResLoginDTO> socialOnboarding(
            @Valid @RequestBody vn.hieu.jobhunter.domain.request.ReqSocialOnboardingDTO req) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        System.out.println(">>> Social Onboarding Request: " + email + ", Role: " + req.getRole());

        if (email.isEmpty()) {
            throw new IdInvalidException("Invalid access token");
        }

        User updatedUser = this.userService.handleSelectRole(email, req.getRole());

        return buildLoginResponse(updatedUser);
    }

    // =================== REFRESH TOKEN ===================
    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
            throws IdInvalidException {

        if ("abc".equals(refresh_token)) {
            throw new IdInvalidException("Refresh token missing in cookies");
        }

        // Check valid
        Jwt decoded = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decoded.getSubject();

        // Check exist
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Invalid refresh token");
        }

        return buildLoginResponse(currentUser);
    }

    // =================== LOGOUT ===================
    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        if (email.isEmpty()) {
            throw new IdInvalidException("Invalid access token");
        }

        // Clear refresh token in DB
        this.userService.updateUserToken(null, email);

        // Clear cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(null);
    }

    // =================== GET ACCOUNT ===================
    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();

        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole(),
                    currentUserDB.getCompany() != null
                            ? new ResLoginDTO.CompanyUser(currentUserDB.getCompany().getId(),
                                    currentUserDB.getCompany().getName())
                            : null,
                    currentUserDB.getAddress(),
                    currentUserDB.getAge(),
                    currentUserDB.getGender());
            userGetAccount.setUser(userLogin);
        }

        return ResponseEntity.ok(userGetAccount);
    }

    // =================== REGISTER ===================
    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(
            @Valid @RequestBody vn.hieu.jobhunter.domain.request.ReqRegisterDTO postUser)
            throws IdInvalidException {

        if (this.userService.isEmailExist(postUser.getEmail())) {
            throw new IdInvalidException("Email " + postUser.getEmail() + " already exists");
        }

        User user = this.userService.handleRegister(postUser);

        // Tạo token và gửi email kích hoạt tài khoản
        String verificationToken = this.userService.generateVerificationToken(user);
        this.userService.sendVerificationEmail(user, verificationToken);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.convertToResCreateUserDTO(user));
    }

    // =================== VERIFY ACCOUNT (EMAIL ACTIVATION) ===================
    @GetMapping("/auth/verify")
    @ApiMessage("Verify account registration")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        User user = this.userService.getUserByVerificationToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Kích hoạt thất bại</title></head><body style='font-family: sans-serif; text-align: center; padding-top: 100px; background-color: #f8fafc;'><div style='max-width: 500px; margin: 0 auto; background: white; padding: 40px; border-radius: 16px; box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); border: 1px border #f1f5f9;'><h2 style='color: #ef4444; font-size: 24px; margin-bottom: 16px;'>Kích hoạt thất bại!</h2><p style='color: #64748b; font-size: 14px; line-height: 1.6; margin-bottom: 24px;'>Mã xác minh không hợp lệ hoặc liên kết đã hết hạn. Vui lòng tiến hành đăng ký lại tài khoản.</p><a href='http://localhost:5173/register' style='display: inline-block; background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 14px; box-shadow: 0 4px 12px rgb(37 99 235 / 0.2);'>Đăng ký lại</a></div></body></html>");
        }

        if ("ACTIVE".equals(user.getStatus())) {
            return ResponseEntity.ok("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Kích hoạt thành công</title></head><body style='font-family: sans-serif; text-align: center; padding-top: 100px; background-color: #f8fafc;'><div style='max-width: 500px; margin: 0 auto; background: white; padding: 40px; border-radius: 16px; box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); border: 1px border #f1f5f9;'><h2 style='color: #10b981; font-size: 24px; margin-bottom: 16px;'>Tài khoản đã kích hoạt</h2><p style='color: #64748b; font-size: 14px; line-height: 1.6; margin-bottom: 24px;'>Tài khoản của bạn đã được kích hoạt từ trước. Bạn có thể truy cập hệ thống và đăng nhập ngay để tìm việc!</p><a href='http://localhost:5173/login' style='display: inline-block; background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 14px; box-shadow: 0 4px 12px rgb(37 99 235 / 0.2);'>Đăng nhập ngay</a></div></body></html>");
        }

        this.userService.activateUser(user);
        return ResponseEntity.ok("<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Kích hoạt thành công</title></head><body style='font-family: sans-serif; text-align: center; padding-top: 100px; background-color: #f8fafc;'><div style='max-width: 500px; margin: 0 auto; background: white; padding: 40px; border-radius: 16px; box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1); border: 1px border #f1f5f9;'><h2 style='color: #10b981; font-size: 24px; margin-bottom: 16px;'>Chúc mừng! Kích hoạt thành công</h2><p style='color: #64748b; font-size: 14px; line-height: 1.6; margin-bottom: 24px;'>Chúc mừng bạn! Tài khoản đã được xác minh thành công. Hãy đăng nhập ngay để bắt đầu hành trình tìm kiếm công việc mơ ước tại JobHunter.</p><a href='http://localhost:5173/login' style='display: inline-block; background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 14px; box-shadow: 0 4px 12px rgb(37 99 235 / 0.2);'>Đăng nhập ngay</a></div></body></html>");
    }

    // =================== CHANGE PASSWORD ===================
    @PostMapping("/auth/change-password")
    @ApiMessage("Change password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequestDTO dto) throws IdInvalidException {
        this.userService.handleChangePassword(dto);
        return ResponseEntity.ok().build();
    }

    // =================== FORGOT PASSWORD ===================
    @PostMapping("/auth/forgot-password")
    @ApiMessage("Forgot password - Request reset token via email")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody vn.hieu.jobhunter.domain.request.ForgotPasswordRequestDTO dto) throws IdInvalidException {
        this.userService.handleForgotPassword(dto.getEmail());
        return ResponseEntity.ok().build();
    }

    // =================== RESET PASSWORD ===================
    @PostMapping("/auth/reset-password")
    @ApiMessage("Reset password - Apply new password using reset token")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody vn.hieu.jobhunter.domain.request.ResetPasswordRequestDTO dto) throws IdInvalidException {
        this.userService.handleResetPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    // =================== PRIVATE HELPER (FIX LỖI GỌI HÀM) ===================
    private ResponseEntity<ResLoginDTO> buildLoginResponse(User user) {
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCompany() != null
                        ? new ResLoginDTO.CompanyUser(user.getCompany().getId(), user.getCompany().getName())
                        : null,
                user.getAddress(),
                user.getAge(),
                user.getGender());
        res.setUser(userLogin);

        // Generate tokens
        String access_token = this.securityUtil.createAccessToken(user.getEmail(), res);
        res.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefreshToken(user.getEmail(), res);

        // Update User Token
        this.userService.updateUserToken(refresh_token, user.getEmail());

        // Set Cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }


}
