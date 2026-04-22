package vn.hieu.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turkraft.springfilter.builder.FilterBuilder;
import vn.hieu.jobhunter.domain.Company;
import vn.hieu.jobhunter.domain.Job;
import vn.hieu.jobhunter.domain.Resume;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.response.ResultPaginationDTO;
import vn.hieu.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hieu.jobhunter.domain.response.resume.ResFetchResumeDTO;
import vn.hieu.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hieu.jobhunter.domain.response.resume.DashboardStatsDTO;
import vn.hieu.jobhunter.repository.JobRepository;
import vn.hieu.jobhunter.repository.ResumeRepository;
import vn.hieu.jobhunter.repository.UserRepository;
import vn.hieu.jobhunter.repository.UserCvRepository;
import vn.hieu.jobhunter.util.SecurityUtil;
import vn.hieu.jobhunter.util.constant.ResumeStateEnum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import vn.hieu.jobhunter.domain.UserCv;

@Service
public class ResumeService {
    @Autowired
    FilterBuilder fb;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;
    private final UserCvRepository userCvRepository;
    private final FileService fileService;

    @Value("${jobhunter.upload-file.base-uri}")
    private String baseURI;

    public ResumeService(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            JobRepository jobRepository,
            EmailService emailService,
            UserCvRepository userCvRepository,
            FileService fileService) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
        this.userCvRepository = userCvRepository;
        this.fileService = fileService;
    }

    public Optional<Resume> fetchById(long id) {
        return this.resumeRepository.findById(id);
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        if (resume.getUser() == null)
            return false;
        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty())
            return false;

        if (resume.getJob() == null)
            return false;
        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty())
            return false;

        return true;
    }

    public ResCreateResumeDTO create(Resume resume) {
        resume = this.resumeRepository.save(resume);

        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedBy(resume.getCreatedBy());
        res.setCreatedAt(resume.getCreatedAt());
        return res;
    }

    @Transactional
    public ResCreateResumeDTO handleApplyJob(long jobId, Long userCvId, MultipartFile file) throws Exception {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Optional<Job> jobOptional = this.jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            throw new RuntimeException("Job not found");
        }
        Job job = jobOptional.get();

        String resumeUrl = null;

        // Option A: Dùng CV có sẵn
        if (userCvId != null) {
            Optional<UserCv> cvOptional = this.userCvRepository.findById(userCvId);
            if (cvOptional.isPresent() && cvOptional.get().getUser().getId() == user.getId()) {
                resumeUrl = cvOptional.get().getUrl();
            } else {
                throw new RuntimeException("CV not found or does not belong to you");
            }
        } 
        // Option B: Upload CV mới
        else if (file != null && !file.isEmpty()) {
            String folder = "resume";
            this.fileService.createDirectory(baseURI + folder);
            resumeUrl = this.fileService.store(file, folder);

            // Tùy chọn: Lưu luôn vào Profile Candidate cho lần sau
            UserCv userCv = new UserCv();
            userCv.setFileName(file.getOriginalFilename());
            userCv.setUrl(resumeUrl);
            userCv.setDefault(false);
            userCv.setUser(user);
            this.userCvRepository.save(userCv);
        } else {
            throw new RuntimeException("Bạn cần chọn CV đã lưu hoặc upload CV mới!");
        }

        Resume resume = new Resume();
        resume.setEmail(user.getEmail());
        resume.setUser(user);
        resume.setJob(job);
        resume.setUrl(resumeUrl);
        resume.setStatus(ResumeStateEnum.PENDING);

        return create(resume);
    }

    public ResUpdateResumeDTO update(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }

    /**
     *  Ánh xạ đầy đủ Resume → ResFetchResumeDTO
     */
    public ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        res.setId(resume.getId());
        res.setEmail(resume.getEmail());
        res.setUrl(resume.getUrl());
        res.setStatus(resume.getStatus());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());

        // User
        if (resume.getUser() != null) {
            res.setUser(new ResFetchResumeDTO.UserResume(
                    resume.getUser().getId(),
                    resume.getUser().getName()));
        }

        // Job + Company
        if (resume.getJob() != null) {
            Job job = resume.getJob();
            Company company = job.getCompany();

            ResFetchResumeDTO.CompanyResume companyDTO = null;
            if (company != null) {
                companyDTO = new ResFetchResumeDTO.CompanyResume(
                        company.getId(),
                        company.getName(),
                        company.getAddress(),
                        company.getLogo(),
                        company.getDescription());
            }

            ResFetchResumeDTO.JobResume jobDTO = new ResFetchResumeDTO.JobResume(
                    job.getId(),
                    job.getName(),
                    job.getLocation(),
                    job.getSalary(),
                    job.getLevel() != null ? job.getLevel().name() : null,
                    companyDTO);

            res.setJob(jobDTO);
        }

        return res;
    }

    public ResultPaginationDTO fetchResumesByCompanyId(long companyId, Pageable pageable) {
        Specification<Resume> spec = (root, query, cb) -> cb.equal(root.get("job").get("company").get("id"), companyId);
        return this.fetchAllResume(spec, pageable);
    }

    public ResultPaginationDTO fetchResumesByJobCreator(String creator, Specification<Resume> spec, Pageable pageable) {
        Specification<Resume> specCreator = (root, query, cb) -> cb.equal(root.get("job").get("createdBy"), creator);
        Specification<Resume> finalSpec = specCreator;
        if (spec != null) {
            finalSpec = finalSpec.and(spec);
        }
        return this.fetchAllResume(finalSpec, pageable);
    }

    public ResultPaginationDTO fetchAllResume(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> page = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<ResFetchResumeDTO> results = page.getContent()
                .stream().map(this::getResume)
                .collect(Collectors.toList());

        ResultPaginationDTO rs = new ResultPaginationDTO();
        rs.setMeta(meta);
        rs.setResult(results);
        return rs;
    }

    @Transactional
    public ResUpdateResumeDTO updateStatus(long resumeId, ResumeStateEnum newStatus, String note) {
        Optional<Resume> optionalResume = this.resumeRepository.findById(resumeId);
        if (optionalResume.isEmpty()) {
            throw new RuntimeException("Không tìm thấy hồ sơ có id = " + resumeId);
        }

        Resume resume = optionalResume.get();

        if (newStatus == ResumeStateEnum.APPROVED) {
            resume.setStatus(newStatus);
            resume.setNote(note != null && !note.isBlank() ? note : "Đã được chấp nhận. Liên hệ ứng viên qua email.");
        } else if (newStatus == ResumeStateEnum.REJECTED) {
            resume.setStatus(newStatus);
            resume.setNote(note != null && !note.isBlank() ? note : "Không đạt yêu cầu.");
        } else {
            resume.setStatus(newStatus);
            resume.setNote(note);
        }

        resume = this.resumeRepository.save(resume);

        // Send email
        if (resume.getUser() != null && resume.getEmail() != null) {
            if (newStatus == ResumeStateEnum.APPROVED || newStatus == ResumeStateEnum.REJECTED) {
                String companyName = (resume.getJob() != null && resume.getJob().getCompany() != null)
                        ? resume.getJob().getCompany().getName()
                        : "Công ty đối tác";
                this.emailService.sendResumeStatusUpdateEmail(
                        resume.getEmail(),
                        resume.getUser().getName(),
                        resume.getJob().getName(),
                        companyName,
                        newStatus.name(),
                        note
                );
            }
        }

        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public ResultPaginationDTO fetchResumeByUser(Specification<Resume> spec, Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        // Create specification to filter by current user's email
        Specification<Resume> emailSpec = (root, query, cb) -> cb.equal(root.get("email"), email);

        // Combine with the provided specification (if any)
        Specification<Resume> finalSpec = emailSpec;
        if (spec != null) {
            finalSpec = finalSpec.and(spec);
        }

        Page<Resume> page = this.resumeRepository.findAll(finalSpec, pageable);

        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        List<ResFetchResumeDTO> results = page.getContent()
                .stream().map(this::getResume)
                .collect(Collectors.toList());

        ResultPaginationDTO rs = new ResultPaginationDTO();
        rs.setMeta(meta);
        rs.setResult(results);
        return rs;
    }

    public DashboardStatsDTO getDashboardStats() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        long total = this.resumeRepository.countByEmail(email);
        long pending = this.resumeRepository.countByEmailAndStatus(email, ResumeStateEnum.PENDING);
        long reviewing = this.resumeRepository.countByEmailAndStatus(email, ResumeStateEnum.REVIEWING);
        long approved = this.resumeRepository.countByEmailAndStatus(email, ResumeStateEnum.APPROVED);
        long rejected = this.resumeRepository.countByEmailAndStatus(email, ResumeStateEnum.REJECTED);

        return new DashboardStatsDTO(total, pending, reviewing, approved, rejected);
    }
}
