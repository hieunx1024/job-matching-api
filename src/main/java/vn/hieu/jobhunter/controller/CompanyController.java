package vn.hieu.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import lombok.Getter;
import lombok.Setter;
import vn.hieu.jobhunter.domain.Company;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.response.ResultPaginationDTO;
import vn.hieu.jobhunter.service.CompanyService;
import vn.hieu.jobhunter.service.RoleService;
import vn.hieu.jobhunter.service.UserService;
import vn.hieu.jobhunter.util.annotation.ApiMessage;
import vn.hieu.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;
    private final UserService userService;
    private final RoleService roleService;

    public CompanyController(CompanyService companyService, UserService userService, RoleService roleService) {
        this.companyService = companyService;
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/companies")
    @ApiMessage("Create company")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCompany(@Valid @RequestBody Company reqCompany) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.companyService.handleCreateCompany(reqCompany));
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch companies with pagination")
    public ResponseEntity<ResultPaginationDTO> getCompany(
            @Filter Specification<Company> spec, Pageable pageable) {

        // Lấy thông tin user hiện tại từ token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.handleGetUserByUsername(username);
        long idRole = user.getRole().getId();
        System.out.println("ID cua role " + idRole);
        System.out.println(user.getId());
        System.out.println(username);
        ResultPaginationDTO result;
        long countPermissionsByRoleId = roleService.countPermissionsByRoleId(idRole);
        System.out.println(countPermissionsByRoleId);
        boolean permissionVsrole = roleService.permissionVsRole(idRole);
        System.out.println("312312312312312312313");
        System.out.println(permissionVsrole);

        if (permissionVsrole) {
            // Admin xem tất cả
            result = this.companyService.handleGetCompany(spec, pageable);
        } else {
            // User thường chỉ xem company do mình tạo
            result = this.companyService.fetchCompanyById(user.getCompany().getId(), pageable);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/companies/public")
    @ApiMessage("Fetch all companies (no role check)")
    public ResponseEntity<ResultPaginationDTO> getAllCompaniesPublic(
            @Filter Specification<Company> spec,
            Pageable pageable) {

        // Gọi trực tiếp hàm trong service, không kiểm tra role
        ResultPaginationDTO result = this.companyService.handleGetCompany(spec, pageable);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/companies")
    @ApiMessage("Update company")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company reqCompany) throws IdInvalidException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.handleGetUserByUsername(username);
        long idRole = user.getRole().getId();
        boolean isAdmin = roleService.permissionVsRole(idRole);

        if (!isAdmin) {
            if (user.getCompany() == null) {
                throw new IdInvalidException("User does not belong to any company");
            }
            if (user.getCompany().getId() != reqCompany.getId()) {
                throw new IdInvalidException("You do not have permission to update this company");
            }
        }

        Company updatedCompany = this.companyService.handleUpdateCompany(reqCompany);
        return ResponseEntity.ok(updatedCompany);
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete company")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Fetch company by id")
    public ResponseEntity<Company> fetchCompanyById(@PathVariable("id") long id) {
        Optional<Company> cOptional = this.companyService.findById(id);
        return ResponseEntity.ok().body(cOptional.get());
    }

    /**
     * 📌 HR đăng ký công ty mới (chỉ được đăng ký 1 lần)
     */
    @PostMapping("/companies/register")
    @ApiMessage("HR registers a new company")
    public ResponseEntity<?> registerCompany(@Valid @RequestBody Company reqCompany) {
        try {
            // Lấy thông tin user hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.handleGetUserByUsername(username);

            // Gọi service để đăng ký công ty
            Company registeredCompany = companyService.registerCompany(currentUser, reqCompany);

            return ResponseEntity.status(HttpStatus.CREATED).body(registeredCompany);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * 📌 HR cập nhật thông tin công ty của mình
     */
    @PutMapping("/companies/my-company")
    @ApiMessage("HR updates their company information")
    public ResponseEntity<?> updateMyCompany(@Valid @RequestBody Company reqCompany) {
        try {
            // Lấy thông tin user hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.handleGetUserByUsername(username);

            // Gọi service để cập nhật công ty
            Company updatedCompany = companyService.updateMyCompany(currentUser, reqCompany);

            return ResponseEntity.ok(updatedCompany);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    /**
     * 📌 HR lấy thông tin công ty của mình
     */
    @GetMapping("/companies/my-company")
    @ApiMessage("HR fetches their company information")
    public ResponseEntity<?> getMyCompany() {
        // Lấy thông tin user hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.handleGetUserByUsername(username);

        // Lấy công ty của HR
        Company myCompany = companyService.getMyCompany(currentUser);

        if (myCompany == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Bạn chưa đăng ký công ty nào"));
        }

        return ResponseEntity.ok(myCompany);
    }

    // Inner class for error response
    @Getter
    @Setter
    private static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
