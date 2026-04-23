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
import vn.hieu.jobhunter.domain.CompanyRegistration;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.response.ResultPaginationDTO;
import vn.hieu.jobhunter.service.CompanyRegistrationService;
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

    public CompanyRegistrationController(CompanyRegistrationService registrationService, UserService userService,
            RoleService roleService) {
        this.registrationService = registrationService;
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * User submits a new company registration request.
     */
    @PostMapping
    @ApiMessage("Create new company registration request")
    public ResponseEntity<CompanyRegistration> createRegistration(
            @Valid @RequestBody CompanyRegistration reqRegistration) {

        // Assign current user to request
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.handleGetUserByUsername(username);
        reqRegistration.setUser(currentUser);

        CompanyRegistration created = registrationService.handleCreateRegistration(reqRegistration);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Fetch list of company registration requests (permission based).
     * - Admin: view all
     * - User: view own requests only
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
     * Admin updates registration status (approve or reject).
     */
    @PutMapping("/{id}/status")
    @ApiMessage("Update company registration status (approve or reject)")
    public ResponseEntity<?> updateRegistrationStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status,
            @RequestBody(required = false) String rejectionReason) {

        // Convert status string to enum
        RegistrationStatus registrationStatus;
        try {
            registrationStatus = RegistrationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid status. Must be APPROVED or REJECTED.");
        }

        // Update reason only when rejected
        String finalReason = null;
        if (registrationStatus == RegistrationStatus.REJECTED) {
            finalReason = (rejectionReason != null && !rejectionReason.trim().isEmpty())
                    ? rejectionReason.trim()
                    : "No specific reason provided.";
        }

        // Update registration status
        CompanyRegistration updated = registrationService.handleUpdateStatus(id, registrationStatus, finalReason);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Company registration not found");
        }

        return ResponseEntity.ok(updated);

    }

    /**
     * Fetch registration detail by ID.
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
     * Delete company registration request.
     */
    @DeleteMapping("/{id}")
    @ApiMessage("Delete company registration request")
    public ResponseEntity<?> deleteRegistration(@PathVariable("id") Long id) {
        registrationService.handleDeleteRegistration(id);
        return ResponseEntity.ok().build();
    }
}
