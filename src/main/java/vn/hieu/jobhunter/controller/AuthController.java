//package vn.hieu.jobhunter.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.web.bind.annotation.CookieValue;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import jakarta.validation.Valid;
//
//import vn.hieu.jobhunter.domain.User;
//import vn.hieu.jobhunter.domain.request.GoogleLoginRequest;
//import vn.hieu.jobhunter.domain.request.ReqLoginDTO;
//import vn.hieu.jobhunter.domain.response.LoginResponse;
//import vn.hieu.jobhunter.domain.response.ResCreateUserDTO;
//import vn.hieu.jobhunter.domain.response.ResLoginDTO;
//import vn.hieu.jobhunter.domain.response.ResUpdateUserDTO;
//import vn.hieu.jobhunter.domain.response.ChangePasswordRequest.ChangePasswordRequest;
//import vn.hieu.jobhunter.service.GoogleAuthService;
//import vn.hieu.jobhunter.service.UserService;
//import vn.hieu.jobhunter.util.SecurityUtil;
//import vn.hieu.jobhunter.util.annotation.ApiMessage;
//import vn.hieu.jobhunter.util.error.IdInvalidException;
//
//@RestController
//@RequestMapping("/api/v1")
//public class AuthController {
//
//    private final AuthenticationManagerBuilder authenticationManagerBuilder;
//    private final SecurityUtil securityUtil;
//    private final UserService userService;
//    private final PasswordEncoder passwordEncoder;
//
//    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
//    private long refreshTokenExpiration;
//    private final GoogleAuthService googleAuthService;
//
//
//    public AuthController(
//            AuthenticationManagerBuilder authenticationManagerBuilder,
//            SecurityUtil securityUtil,
//            UserService userService,
//            PasswordEncoder passwordEncoder, GoogleAuthService googleAuthService) {
//        this.authenticationManagerBuilder = authenticationManagerBuilder;
//        this.securityUtil = securityUtil;
//        this.userService = userService;
//        this.passwordEncoder = passwordEncoder;
//        this.googleAuthService = googleAuthService;
//    }
//
//    // =================== LOGIN ===================
//    @PostMapping("/auth/login")
//    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDto) {
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(),
//                loginDto.getPassword());
//
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        ResLoginDTO res = new ResLoginDTO();
//        User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
//
//        if (currentUserDB != null) {
//            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
//                    currentUserDB.getId(),
//                    currentUserDB.getEmail(),
//                    currentUserDB.getName(),
//                    currentUserDB.getRole());
//            res.setUser(userLogin);
//        }
//
//        // access token
//        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
//        res.setAccessToken(access_token);
//
//        // refresh token
//        String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);
//
//        // save refresh token
//        this.userService.updateUserToken(refresh_token, loginDto.getUsername());
//
//        // set cookie
//        ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh_token)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(refreshTokenExpiration)
//                .build();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .body(res);
//    }
//
//    @PostMapping("/google")
//    public LoginResponse loginGoogle(@RequestBody GoogleLoginRequest req) {
//
//        var payload = googleAuthService.verify(req.getIdToken());
//
//        User user = userService.findOrCreateGoogleUser(payload);
//
//        String accessToken = securityUtil.generateToken(user);
//        String refreshToken = securityUtil.generateRefreshToken(user);
//
//        userService.updateUserToken(refreshToken, user.getEmail());
//
//        return new LoginResponse(accessToken, refreshToken);
//    }
//
//
//    // =================== GET ACCOUNT ===================
//    @GetMapping("/auth/account")
//    @ApiMessage("fetch account")
//    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
//        String email = SecurityUtil.getCurrentUserLogin().orElse("");
//
//        User currentUserDB = this.userService.handleGetUserByUsername(email);
//        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
//
//        if (currentUserDB != null) {
//            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
//                    currentUserDB.getId(),
//                    currentUserDB.getEmail(),
//                    currentUserDB.getName(),
//                    currentUserDB.getRole());
//            userGetAccount.setUser(userLogin);
//        }
//
//        return ResponseEntity.ok(userGetAccount);
//    }
//
//    // =================== REFRESH TOKEN ===================
//    @GetMapping("/auth/refresh")
//    @ApiMessage("Get User by refresh token")
//    public ResponseEntity<ResLoginDTO> getRefreshToken(
//            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
//            throws IdInvalidException {
//
//        if (refresh_token.equals("abc")) {
//            throw new IdInvalidException("Bạn không có refresh token ở cookie");
//        }
//
//        Jwt decoded = this.securityUtil.checkValidRefreshToken(refresh_token);
//        String email = decoded.getSubject();
//
//        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
//        if (currentUser == null) {
//            throw new IdInvalidException("Refresh Token không hợp lệ");
//        }
//
//        // issue new tokens
//        ResLoginDTO res = new ResLoginDTO();
//        User user = this.userService.handleGetUserByUsername(email);
//
//        if (user != null) {
//            res.setUser(new ResLoginDTO.UserLogin(
//                    user.getId(),
//                    user.getEmail(),
//                    user.getName(),
//                    user.getRole()));
//        }
//
//        String access_token = this.securityUtil.createAccessToken(email, res);
//        res.setAccessToken(access_token);
//
//        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
//
//        this.userService.updateUserToken(new_refresh_token, email);
//
//        ResponseCookie cookie = ResponseCookie.from("refresh_token", new_refresh_token)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(refreshTokenExpiration)
//                .build();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .body(res);
//    }
//
//    // =================== LOGOUT ===================
//    @PostMapping("/auth/logout")
//    @ApiMessage("Logout User")
//    public ResponseEntity<Void> logout() throws IdInvalidException {
//
//        String email = SecurityUtil.getCurrentUserLogin().orElse("");
//
//        if (email.isEmpty()) {
//            throw new IdInvalidException("Access Token không hợp lệ");
//        }
//
//        this.userService.updateUserToken(null, email);
//
//        ResponseCookie cookie = ResponseCookie.from("refresh_token", null)
//                .httpOnly(true)
//                .secure(true)
//                .path("/")
//                .maxAge(0)
//                .build();
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.SET_COOKIE, cookie.toString())
//                .body(null);
//    }
//
//    // =================== REGISTER ===================
//    @PostMapping("/auth/register")
//    @ApiMessage("Register a new user")
//    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postUser)
//            throws IdInvalidException {
//
//        if (this.userService.isEmailExist(postUser.getEmail())) {
//            throw new IdInvalidException("Email " + postUser.getEmail() + " đã tồn tại.");
//        }
//
//        postUser.setPassword(passwordEncoder.encode(postUser.getPassword()));
//
//        User user = this.userService.handleCreateUser(postUser);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(this.userService.convertToResCreateUserDTO(user));
//    }
//
//    // =================== CHANGE PASSWORD ===================
//    @PutMapping("/auth/change-password")
//    @ApiMessage("Change user password")
//    public ResponseEntity<ResUpdateUserDTO> changePassword(
//            @RequestBody ChangePasswordRequest req) throws IdInvalidException {
//
//        String email = SecurityUtil.getCurrentUserLogin().orElse("");
//
//        if (email.isEmpty()) {
//            throw new IdInvalidException("Access Token không hợp lệ hoặc đã hết hạn.");
//        }
//
//        User user = this.userService.handleGetUserByUsername(email);
//        if (user == null) {
//            throw new IdInvalidException("Người dùng không tồn tại.");
//        }
//
//        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
//            throw new IdInvalidException("Mật khẩu cũ không chính xác.");
//        }
//
//        if (req.getOldPassword().equals(req.getNewPassword())) {
//            throw new IdInvalidException("Mật khẩu mới không được giống mật khẩu cũ.");
//        }
//
//        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
//        user = this.userService.handleUpdateUser(user);
//
//        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(user));
//    }
//
//    // =================== LOGIN GOOGLE ===================
//    // @PostMapping("/auth/login-google")
//    // @ApiMessage("Login with Google")
//    // public ResponseEntity<ResLoginDTO> loginGoogle(@RequestBody Map<String,
//    // String> body)
//    // throws Exception {
//
//    // String idTokenString = body.get("token");
//    // if (idTokenString == null) {
//    // throw new IdInvalidException("Thiếu Google token");
//    // }
//
//    // // ⚡ Sửa: set audience = Google clientId của bạn
//    // GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//    // new NetHttpTransport(), new GsonFactory())
//    // .setAudience(List.of("YOUR_GOOGLE_CLIENT_ID"))
//    // .build();
//
//    // GoogleIdToken idToken = verifier.verify(idTokenString);
//    // if (idToken == null) {
//    // throw new IdInvalidException("Token Google không hợp lệ");
//    // }
//
//    // GoogleIdToken.Payload payload = idToken.getPayload();
//    // String email = payload.getEmail();
//    // String name = (String) payload.get("name");
//
//    // // check user tồn tại
//    // User user = userService.handleGetUserByUsername(email);
//
//    // if (user == null) {
//    // user = new User();
//    // user.setEmail(email);
//    // user.setName(name);
//    // user.setPassword(passwordEncoder.encode("GOOGLE_USER")); // dummy password
//    // user.setProvider("GOOGLE");
//    // user = userService.handleCreateUser(user);
//    // }
//
//    // // build response
//    // ResLoginDTO res = new ResLoginDTO();
//    // res.setUser(new ResLoginDTO.UserLogin(
//    // user.getId(),
//    // user.getEmail(),
//    // user.getName(),
//    // user.getRole()));
//
//    // // create access + refresh token
//    // String access_token = this.securityUtil.createAccessToken(email, res);
//    // res.setAccessToken(access_token);
//
//    // String refresh_token = this.securityUtil.createRefreshToken(email, res);
//    // this.userService.updateUserToken(refresh_token, email);
//
//    // // set cookie
//    // ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh_token)
//    // .httpOnly(true)
//    // .secure(true)
//    // .path("/")
//    // .maxAge(refreshTokenExpiration)
//    // .build();
//
//    // return ResponseEntity.ok()
//    // .header(HttpHeaders.SET_COOKIE, cookie.toString())
//    // .body(res);
//    // }
//
//}



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
import vn.hieu.jobhunter.domain.response.ResCreateUserDTO;
import vn.hieu.jobhunter.domain.response.ResLoginDTO;
import vn.hieu.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hieu.jobhunter.domain.response.ChangePasswordRequest.ChangePasswordRequest;
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
        // 1. Nạp object authentication vào Security Context
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Lấy thông tin user từ DB
        User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());

        // 3. Tạo Token và trả về (Sử dụng hàm chung để tránh lỗi)
        return buildLoginResponse(currentUserDB);
    }

    // =================== LOGIN GOOGLE ===================
    @PostMapping("/auth/google")
    @ApiMessage("Login with Google")
    public ResponseEntity<ResLoginDTO> loginGoogle(@RequestBody GoogleLoginRequest req) {
        // 1. Verify Google Token (Lấy payload từ Google)
        var payload = googleAuthService.verify(req.getIdToken());

        // 2. Tìm hoặc tạo user mới từ Payload này
        // LƯU Ý: Nếu dòng này báo lỗi đỏ, xem Bước 2 bên dưới
        User user = userService.findOrCreateGoogleUser(payload);

        // 3. Tạo Token và trả về
        return buildLoginResponse(user);
    }

    // =================== REFRESH TOKEN ===================
    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
            throws IdInvalidException {

        if ("abc".equals(refresh_token)) {
            throw new IdInvalidException("Bạn không có refresh token ở cookie");
        }

        // Check valid
        Jwt decoded = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decoded.getSubject();

        // Check exist
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        return buildLoginResponse(currentUser);
    }

    // =================== LOGOUT ===================
    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        if (email.isEmpty()) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // Xóa refresh token trong DB
        this.userService.updateUserToken(null, email);

        // Xóa Cookie (Set value là rỗng, maxAge = 0)
        // FIX LỖI: Không truyền null vào value, truyền ""
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
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
                    currentUserDB.getRole());
            userGetAccount.setUser(userLogin);
        }

        return ResponseEntity.ok(userGetAccount);
    }

    // =================== REGISTER ===================
    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postUser)
            throws IdInvalidException {

        if (this.userService.isEmailExist(postUser.getEmail())) {
            throw new IdInvalidException("Email " + postUser.getEmail() + " đã tồn tại.");
        }

        postUser.setPassword(passwordEncoder.encode(postUser.getPassword()));

        User user = this.userService.handleCreateUser(postUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.convertToResCreateUserDTO(user));
    }

    // =================== CHANGE PASSWORD ===================
    @PutMapping("/auth/change-password")
    @ApiMessage("Change user password")
    public ResponseEntity<ResUpdateUserDTO> changePassword(
            @RequestBody ChangePasswordRequest req) throws IdInvalidException {

        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        if (email.isEmpty()) {
            throw new IdInvalidException("Access Token không hợp lệ hoặc đã hết hạn.");
        }

        User user = this.userService.handleGetUserByUsername(email);
        if (user == null) {
            throw new IdInvalidException("Người dùng không tồn tại.");
        }

        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new IdInvalidException("Mật khẩu cũ không chính xác.");
        }

        if (req.getOldPassword().equals(req.getNewPassword())) {
            throw new IdInvalidException("Mật khẩu mới không được giống mật khẩu cũ.");
        }

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user = this.userService.handleUpdateUser(user);

        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(user));
    }

    // =================== PRIVATE HELPER (FIX LỖI GỌI HÀM) ===================
    private ResponseEntity<ResLoginDTO> buildLoginResponse(User user) {
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole());
        res.setUser(userLogin);

        // FIX LỖI: Gọi đúng tên hàm createAccessToken
        String access_token = this.securityUtil.createAccessToken(user.getEmail(), res);
        res.setAccessToken(access_token);

        // FIX LỖI: Gọi đúng tên hàm createRefreshToken
        String refresh_token = this.securityUtil.createRefreshToken(user.getEmail(), res);

        // Update User Token
        this.userService.updateUserToken(refresh_token, user.getEmail());

        // Set Cookie
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    // ... (Giữ nguyên các hàm Account, Register, ChangePassword cũ) ...
    // Bạn có thể copy lại phần getAccount, register, changePassword từ file cũ
    // vì phần đó logic không thay đổi nhiều.
}
