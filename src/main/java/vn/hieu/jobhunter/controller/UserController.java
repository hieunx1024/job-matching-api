package vn.hieu.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.response.ResCreateUserDTO;
import vn.hieu.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hieu.jobhunter.domain.response.ResUserDTO;
import vn.hieu.jobhunter.domain.response.ResultPaginationDTO;
import vn.hieu.jobhunter.service.RoleService;
import vn.hieu.jobhunter.service.UserService;
import vn.hieu.jobhunter.util.annotation.ApiMessage;
import vn.hieu.jobhunter.util.error.IdInvalidException;
import vn.hieu.jobhunter.domain.request.ReqSelectRoleDTO;
import vn.hieu.jobhunter.domain.response.ResLoginDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PatchMapping;
import vn.hieu.jobhunter.util.SecurityUtil;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public UserController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder,
            SecurityUtil securityUtil) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postManUser)
            throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + postManUser.getEmail() + "đã tồn tại, vui lòng sử dụng email khác.");
        }

        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User ericUser = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(ericUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }

        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.userService.convertToResUserDTO(fetchUser));
    }

    // fetch all users
    @GetMapping("/users")
    @ApiMessage("Fetch all users with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {

        // Lấy user hiện tại từ token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.handleGetUserByUsername(username);

        ResultPaginationDTO result;

        // ✅ Nếu role hoặc role_id = null → coi như user thường
        if (currentUser.getRole() == null || currentUser.getRole().getId() == 0) {

            result = this.userService.fetchAllUserByCreatorOrSelf(
                    currentUser.getEmail(),
                    currentUser.getId(),
                    pageable);

        } else {
            long idRole = currentUser.getRole().getId();

            System.out.println("ID role: " + idRole);
            System.out.println("User hiện tại: " + currentUser.getEmail());

            boolean isAdmin = roleService.permissionVsRole(idRole);

            if (isAdmin) {
                // ✅ Admin xem tất cả user
                result = this.userService.fetchAllUser(spec, pageable);
            } else {
                // ✅ User thường
                result = this.userService.fetchAllUserByCreatorOrSelf(
                        currentUser.getEmail(),
                        currentUser.getId(),
                        pageable);
            }
        }

        return ResponseEntity.ok(result);
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User ericUser = this.userService.handleUpdateUser(user);
        if (ericUser == null) {
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(ericUser));
    }

    @PatchMapping("/users/select-role")
    @ApiMessage("Select role for user")
    public ResponseEntity<ResLoginDTO> selectRole(@RequestBody ReqSelectRoleDTO req) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (email.isEmpty()) {
            throw new IdInvalidException("Access token invalid");
        }

        User updatedUser = this.userService.handleSelectRole(email, req.getRoleName());
        return buildLoginResponse(updatedUser);
    }

    @PatchMapping("/users/profile")
    @ApiMessage("Update user profile")
    public ResponseEntity<ResUserDTO> updateProfile(@RequestBody User profileUpdate) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (email.isEmpty()) {
            throw new IdInvalidException("Access token invalid");
        }

        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser == null) {
            throw new IdInvalidException("User not found");
        }

        // Update allowed fields
        if (profileUpdate.getName() != null) {
            currentUser.setName(profileUpdate.getName());
        }
        if (profileUpdate.getAddress() != null) {
            currentUser.setAddress(profileUpdate.getAddress());
        }
        if (profileUpdate.getAge() != 0) {
            currentUser.setAge(profileUpdate.getAge());
        }
        if (profileUpdate.getGender() != null) {
            currentUser.setGender(profileUpdate.getGender());
        }

        User updatedUser = this.userService.handleUpdateUser(currentUser);
        return ResponseEntity.ok(this.userService.convertToResUserDTO(updatedUser));
    }

    private ResponseEntity<ResLoginDTO> buildLoginResponse(User user) {
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCompany() != null
                        ? new ResLoginDTO.CompanyUser(user.getCompany().getId(), user.getCompany().getName())
                        : null);
        res.setUser(userLogin);

        String access_token = this.securityUtil.createAccessToken(user.getEmail(), res);
        res.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefreshToken(user.getEmail(), res);

        this.userService.updateUserToken(refresh_token, user.getEmail());

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
