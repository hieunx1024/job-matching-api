package vn.hieu.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hieu.jobhunter.domain.Company;
import vn.hieu.jobhunter.domain.CompanyRegistration;
import vn.hieu.jobhunter.domain.Role;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.response.ResultPaginationDTO;
import vn.hieu.jobhunter.service.CompanyRegistrationService;
import vn.hieu.jobhunter.service.CompanyService;
import vn.hieu.jobhunter.service.RoleService;
import vn.hieu.jobhunter.service.UserService;
import vn.hieu.jobhunter.util.annotation.ApiMessage;
import vn.hieu.jobhunter.util.constant.RegistrationStatus;

@RestController
@RequestMapping("/api/v1/company-registrations")
public class CompanyRegistrationController {

    private final CompanyRegistrationService registrationService;
    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;

    public CompanyRegistrationController(CompanyRegistrationService registrationService, UserService userService,
            RoleService roleService, CompanyService companyService) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.roleService = roleService;
        this.companyService = companyService;
    }

    /**
     * 👤 Người dùng gửi yêu cầu đăng ký công ty mới
     */
    @PostMapping
    @ApiMessage("Create new company registration request")
    public ResponseEntity<CompanyRegistration> createRegistration(
            @Valid @RequestBody CompanyRegistration reqRegistration) {

        // Gán user đang đăng nhập cho yêu cầu
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.handleGetUserByUsername(username);
        reqRegistration.setUser(currentUser);

        CompanyRegistration created = registrationService.handleCreateRegistration(reqRegistration);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * 🔍 Lấy danh sách yêu cầu đăng ký công ty (có phân quyền)
     * - Admin: xem tất cả
     * - User: chỉ xem của chính mình
     */
    @GetMapping
    @ApiMessage("Fetch company registration requests with pagination")
    public ResponseEntity<ResultPaginationDTO> getRegistrations(
            @Filter Specification<CompanyRegistration> spec, Pageable pageable) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.handleGetUserByUsername(username);

        long roleId = user.getRole().getId();
        boolean isAdmin = roleService.permissionVsRole(roleId);

        ResultPaginationDTO result;
        if (isAdmin) {
            result = registrationService.handleGetRegistrations(spec, pageable);
        } else {
            result = registrationService.fetchRegistrationsByUser(username, pageable);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * ✅ Admin phê duyệt yêu cầu
     */
    /**
     * ⚙️ Admin cập nhật trạng thái yêu cầu đăng ký công ty (phê duyệt hoặc từ chối)
     */
    @PutMapping("/{id}/status")
    @ApiMessage("Update company registration status (approve or reject)")
    public ResponseEntity<?> updateRegistrationStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status,
            @RequestBody(required = false) String rejectionReason) {

        // Chuyển status từ string sang enum
        RegistrationStatus registrationStatus;
        try {
            registrationStatus = RegistrationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid status. Must be APPROVED or REJECTED.");
        }

        // Chỉ cập nhật lý do khi bị từ chối
        String finalReason = null;
        if (registrationStatus == RegistrationStatus.REJECTED) {
            finalReason = (rejectionReason != null && !rejectionReason.trim().isEmpty())
                    ? rejectionReason.trim()
                    : "Không có lý do cụ thể.";
        }

        // Cập nhật trạng thái đăng ký
        CompanyRegistration updated = registrationService.handleUpdateStatus(id, registrationStatus, finalReason);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Company registration not found");
        }

        // Nếu phê duyệt, tạo công ty dựa trên thông tin đăng ký
        if (registrationStatus == RegistrationStatus.APPROVED) {
            Company company = new Company();
            company.setName(updated.getCompanyName());
            company.setDescription(updated.getDescription());
            company.setAddress(updated.getAddress());
            company.setLogo(updated.getLogo());

            User user = userService.fetchUserById(updated.getUser().getId());
            // Gán user tạo công ty với role id = 3

            Optional<Company> companyOptional = this.companyService.findById(user.getCompany().getId());
            user.setCompany(companyOptional.isPresent() ? companyOptional.get() : null);
            Role r = roleService.fetchById(2);
            user.setRole(r);
            // Lưu lại user
            userService.handleUpdateUser(user);
        }
        if (registrationStatus == RegistrationStatus.REJECTED) {
            User user = userService.fetchUserById(updated.getUser().getId());

            // Kiểm tra role và company

            companyService.handleDeleteCompany(user.getCompany().getId());
        }

        return ResponseEntity.ok(updated);

    }

    /**
     * 🔎 Lấy chi tiết một yêu cầu theo ID
     */
    @GetMapping("/{id}")
    @ApiMessage("Fetch company registration detail")
    public ResponseEntity<CompanyRegistration> getRegistrationById(@PathVariable Long id) {
        Optional<CompanyRegistration> registration = registrationService.findById(id);
        if (registration.isPresent()) {
            return ResponseEntity.ok(registration.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * 🗑️ Xóa yêu cầu đăng ký công ty (nếu cần)
     */
    @DeleteMapping("/{id}")
    @ApiMessage("Delete company registration request")
    public ResponseEntity<?> deleteRegistration(@PathVariable("id") Long id) {
        registrationService.handleDeleteRegistration(id);
        return ResponseEntity.ok().build();
    }
}
