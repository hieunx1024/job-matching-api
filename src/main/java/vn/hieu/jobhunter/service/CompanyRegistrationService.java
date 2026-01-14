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
     * 📌 Tạo yêu cầu đăng ký công ty mới (trạng thái mặc định: PENDING)
     */
    public CompanyRegistration handleCreateRegistration(CompanyRegistration registration) {
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setCreatedAt(Instant.now());
        return registrationRepository.save(registration);
    }

    /**
     * 📌 Lấy danh sách tất cả yêu cầu (có phân trang và lọc bằng Specification)
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
     * 📌 Lấy danh sách yêu cầu đăng ký của 1 user cụ thể
     */
    public ResultPaginationDTO fetchRegistrationsByUser(String username, Pageable pageable) {
        Specification<CompanyRegistration> spec = (root, query, cb) -> cb.equal(root.get("createdBy"), username);
        return handleGetRegistrations(spec, pageable);
    }

    /**
     * 📌 Cập nhật trạng thái của yêu cầu đăng ký công ty.
     * Nếu APPROVED → tự động tạo bản ghi trong bảng Company.
     */
    public CompanyRegistration handleUpdateStatus(Long id, RegistrationStatus status, String rejectionReason) {
        Optional<CompanyRegistration> registrationOptional = registrationRepository.findById(id);

        if (registrationOptional.isPresent()) {
            CompanyRegistration reg = registrationOptional.get();
            reg.setStatus(status);
            reg.setRejectionReason(status == RegistrationStatus.REJECTED ? rejectionReason : null);
            reg.setUpdatedAt(Instant.now());

            // ✅ Nếu admin phê duyệt → tạo mới công ty từ thông tin đăng ký
            if (status == RegistrationStatus.APPROVED) {
                Company company = new Company();
                company.setName(reg.getCompanyName());
                company.setDescription(reg.getDescription());
                company.setAddress(reg.getAddress());
                company.setLogo(reg.getLogo());
                company.setCreatedBy(reg.getCreatedBy());
                company.setCreatedAt(Instant.now());

                // 💾 Lưu công ty vào DB trước để có ID
                Company savedCompany = companyRepository.save(company);

                // ✅ Gán công ty mới cho user gửi yêu cầu
                if (reg.getUser() != null && reg.getUser().getId() > 0) {
                    Long userId = reg.getUser().getId();
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        user.setCompany(savedCompany); // gán company_id vừa tạo
                        userRepository.save(user);
                    }
                }
            }

            return registrationRepository.save(reg);
        }

        return null;
    }

    /**
     * 📌 Xóa yêu cầu đăng ký công ty
     */
    public void handleDeleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    /**
     * 📌 Lấy chi tiết một yêu cầu theo ID
     */
    public Optional<CompanyRegistration> findById(Long id) {
        return registrationRepository.findById(id);
    }

    /**
     * 📌 Lấy tất cả yêu cầu theo trạng thái (PENDING, APPROVED, REJECTED)
     */
    public List<CompanyRegistration> findByStatus(RegistrationStatus status) {
        Specification<CompanyRegistration> spec = (root, query, cb) -> cb.equal(root.get("status"), status);
        return registrationRepository.findAll(spec);
    }
}
