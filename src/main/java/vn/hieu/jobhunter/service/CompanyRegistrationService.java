package vn.hieu.jobhunter.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hieu.jobhunter.domain.Company;
import vn.hieu.jobhunter.domain.CompanyRegistration;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.response.ResultPaginationDTO;
import vn.hieu.jobhunter.repository.CompanyRegistrationRepository;
import vn.hieu.jobhunter.repository.CompanyRepository;
import vn.hieu.jobhunter.repository.UserRepository;
import vn.hieu.jobhunter.util.constant.RegistrationStatus;

@Service
public class CompanyRegistrationService {

    private final CompanyRegistrationRepository registrationRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyRegistrationService(
            CompanyRegistrationRepository registrationRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new company registration request with default PENDING status.
     */
    public CompanyRegistration handleCreateRegistration(CompanyRegistration registration) {
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setCreatedAt(Instant.now());
        return registrationRepository.save(registration);
    }

    /**
     * Get paginated and filtered registration requests.
     */
    public ResultPaginationDTO handleGetRegistrations(Specification<CompanyRegistration> spec, Pageable pageable) {
        Page<CompanyRegistration> pageReg = registrationRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageReg.getTotalPages());
        mt.setTotal(pageReg.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageReg.getContent());
        return rs;
    }

    /**
     * Fetch registrations created by a specific user.
     */
    public ResultPaginationDTO fetchRegistrationsByUser(String username, Pageable pageable) {
        Specification<CompanyRegistration> spec = (root, query, cb) -> cb.equal(root.get("createdBy"), username);
        return handleGetRegistrations(spec, pageable);
    }

    /**
     * Update registration status. Automatically creates a Company record if APPROVED.
     */
    public CompanyRegistration handleUpdateStatus(Long id, RegistrationStatus status, String rejectionReason) {
        Optional<CompanyRegistration> registrationOptional = registrationRepository.findById(id);

        if (registrationOptional.isPresent()) {
            CompanyRegistration reg = registrationOptional.get();
            reg.setStatus(status);
            reg.setRejectionReason(status == RegistrationStatus.REJECTED ? rejectionReason : null);
            reg.setUpdatedAt(Instant.now());

            // Create new company from registration data if approved
            if (status == RegistrationStatus.APPROVED) {
                Company company = new Company();
                company.setName(reg.getCompanyName());
                company.setDescription(reg.getDescription());
                company.setAddress(reg.getAddress());
                company.setLogo(reg.getLogo());
                company.setFacebookLink(reg.getFacebookLink());
                company.setGithubLink(reg.getGithubLink());
                company.setCreatedBy(reg.getCreatedBy());
                company.setCreatedAt(Instant.now());

                // Save company to generate ID
                Company savedCompany = companyRepository.save(company);

                // Assign new company to the requesting user
                if (reg.getUser() != null && reg.getUser().getId() > 0) {
                    Long userId = reg.getUser().getId();
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        user.setCompany(savedCompany); // assign generated company_id
                        userRepository.save(user);
                    }
                }
            }

            return registrationRepository.save(reg);
        }

        return null;
    }

    /**
     * Delete a registration request by ID.
     */
    public void handleDeleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    /**
     * Find a single registration by ID.
     */
    public Optional<CompanyRegistration> findById(Long id) {
        return registrationRepository.findById(id);
    }

    /**
     * Find all registrations by status.
     */
    public List<CompanyRegistration> findByStatus(RegistrationStatus status) {
        Specification<CompanyRegistration> spec = (root, query, cb) -> cb.equal(root.get("status"), status);
        return registrationRepository.findAll(spec);
    }
}
