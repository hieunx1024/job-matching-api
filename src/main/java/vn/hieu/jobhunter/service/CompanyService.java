package vn.hieu.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hieu.jobhunter.domain.Company;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.response.ResultPaginationDTO;
import vn.hieu.jobhunter.repository.CompanyRepository;
import vn.hieu.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company c) {
        return this.companyRepository.save(c);
    }

    public ResultPaginationDTO handleGetCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pCompany.getTotalPages());
        mt.setTotal(pCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pCompany.getContent());
        return rs;
    }

    public ResultPaginationDTO fetchCompanyById(long companyId, Pageable pageable) {
        // Tạo điều kiện lọc (Specification)
        Specification<Company> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"),
                companyId);

        // Gọi hàm handleGetCompany() để tái sử dụng logic phân trang
        return this.handleGetCompany(spec, pageable);
    }

    public Company handleUpdateCompany(Company c) {
        Optional<Company> companyOptional = this.companyRepository.findById(c.getId());
        if (companyOptional.isPresent()) {
            Company currentCompany = companyOptional.get();
            currentCompany.setLogo(c.getLogo());
            currentCompany.setName(c.getName());
            currentCompany.setDescription(c.getDescription());
            currentCompany.setAddress(c.getAddress());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        Optional<Company> comOptional = this.companyRepository.findById(id);
        if (comOptional.isPresent()) {
            Company com = comOptional.get();
            // fetch all users belong to this company
            List<User> users = this.userRepository.findByCompany(com);
            // set company=null cho tất cả user
            for (User u : users) {
                u.setCompany(null);
                u.setRole(null);
            }
            // lưu lại các user đã cập nhật
            this.userRepository.saveAll(users);
        }

        // xóa công ty
        this.companyRepository.deleteById(id);
    }

    public Optional<Company> findById(long id) {
        return this.companyRepository.findById(id);
    }

    /**
     * 📌 HR đăng ký công ty mới (chỉ được đăng ký 1 lần)
     * 
     * @param currentUser - User hiện tại (HR)
     * @param reqCompany  - Thông tin công ty cần đăng ký
     * @return Company đã tạo
     * @throws IllegalStateException nếu HR đã có công ty
     */
    @org.springframework.transaction.annotation.Transactional
    public Company registerCompany(User currentUser, Company reqCompany) {
        // Kiểm tra xem HR đã có công ty chưa
        if (currentUser.getCompany() != null) {
            throw new IllegalStateException("Bạn đã đại diện cho một công ty, không thể đăng ký thêm");
        }

        // Tạo công ty mới
        Company newCompany = new Company();
        newCompany.setName(reqCompany.getName());
        newCompany.setDescription(reqCompany.getDescription());
        newCompany.setAddress(reqCompany.getAddress());
        newCompany.setLogo(reqCompany.getLogo());
        newCompany.setVerified(false); // Mặc định chưa được xác thực

        // Lưu công ty
        Company savedCompany = this.companyRepository.save(newCompany);

        // Gán công ty cho HR
        currentUser.setCompany(savedCompany);
        this.userRepository.save(currentUser);

        return savedCompany;
    }

    /**
     * 📌 HR cập nhật thông tin công ty của mình
     * 
     * @param currentUser - User hiện tại (HR)
     * @param reqCompany  - Thông tin công ty cần cập nhật
     * @return Company đã cập nhật
     * @throws IllegalStateException nếu HR chưa có công ty
     */
    @org.springframework.transaction.annotation.Transactional
    public Company updateMyCompany(User currentUser, Company reqCompany) {
        // Kiểm tra xem HR đã có công ty chưa
        if (currentUser.getCompany() == null) {
            throw new IllegalStateException("Bạn chưa đăng ký công ty nào");
        }

        // Lấy công ty hiện tại của HR
        Company currentCompany = currentUser.getCompany();

        // Cập nhật thông tin (không cho phép thay đổi ID)
        currentCompany.setName(reqCompany.getName());
        currentCompany.setDescription(reqCompany.getDescription());
        currentCompany.setAddress(reqCompany.getAddress());
        currentCompany.setLogo(reqCompany.getLogo());

        // Lưu lại
        return this.companyRepository.save(currentCompany);
    }

    /**
     * 📌 Lấy thông tin công ty của HR hiện tại
     * 
     * @param currentUser - User hiện tại (HR)
     * @return Company hoặc null nếu chưa có
     */
    public Company getMyCompany(User currentUser) {
        return currentUser.getCompany();
    }
}
