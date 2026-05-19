package vn.hieu.jobhunter.service.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import vn.hieu.jobhunter.domain.Job;
import vn.hieu.jobhunter.domain.Skill;
import vn.hieu.jobhunter.domain.dto.chat.ChatResponse;
import vn.hieu.jobhunter.repository.JobRepository;
import vn.hieu.jobhunter.domain.Company;
import vn.hieu.jobhunter.repository.CompanyRepository;

import jakarta.persistence.criteria.Join;
import vn.hieu.jobhunter.domain.Resume;
import vn.hieu.jobhunter.repository.ResumeRepository;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class JobSearchFunction {

    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;
    private final CompanyRepository companyRepository;
    private final String frontendUrl;

    public JobSearchFunction(JobRepository jobRepository, ResumeRepository resumeRepository, CompanyRepository companyRepository, @org.springframework.beans.factory.annotation.Value("${jobhunter.frontend.url}") String frontendUrl) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.companyRepository = companyRepository;
        this.frontendUrl = frontendUrl;
    }

    public record JobSearchRequest(String location, String skill, Double minSalary, Double maxSalary, String level, String companyName) {
    }

    public record ApplicantStatusRequest(String jobTitle) {
    }

    public record ApplicantStatusDTO(String jobName, String companyName, String status, String note) {
    }

    public record CompanyInfoRequest(String companyName) {
    }

    public record CompanyInfoDTO(String name, String address, String description, String logo) {
    }

    @Bean
    @Description("Get information about a company by its name.")
    public Function<CompanyInfoRequest, CompanyInfoDTO> getCompanyInfo() {
        return request -> {
            System.out.println("AI called getCompanyInfo with: " + request);
            
            if (request.companyName() == null || request.companyName().isBlank()) {
                return null;
            }

            Specification<Company> spec = (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + request.companyName().toLowerCase() + "%");
            
            List<Company> companies = companyRepository.findAll(spec);
            
            if (companies.isEmpty()) {
                return null;
            }
            
            Company company = companies.get(0); // Get the first match
            
            return new CompanyInfoDTO(
                    company.getName(),
                    company.getAddress(),
                    company.getDescription(),
                    company.getLogo()
            );
        };
    }

    @Bean
    @Description("Get the status of the current user's job applications. Optionally filter by job title.")
    public Function<ApplicantStatusRequest, List<ApplicantStatusDTO>> getApplicantStatus() {
        return request -> {
            String email = vn.hieu.jobhunter.util.SecurityUtil.getCurrentUserLogin().orElse("");
            if (email.isBlank()) {
                return List.of(new ApplicantStatusDTO("N/A", "N/A", "Chưa đăng nhập", "Vui lòng đăng nhập để kiểm tra trạng thái."));
            }
            System.out.println("AI called getApplicantStatus for user: " + email + " with request: " + request);

            Specification<Resume> spec = (root, query, cb) -> cb.equal(root.get("email"), email);

            if (request.jobTitle() != null && !request.jobTitle().isBlank()) {
                spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("job").get("name")), "%" + request.jobTitle().toLowerCase() + "%"));
            }

            List<Resume> resumes = resumeRepository.findAll(spec);

            return resumes.stream().map(resume -> new ApplicantStatusDTO(
                    resume.getJob() != null ? resume.getJob().getName() : "N/A",
                    resume.getJob() != null && resume.getJob().getCompany() != null ? resume.getJob().getCompany().getName() : "N/A",
                    resume.getStatus() != null ? resume.getStatus().name() : "N/A",
                    resume.getNote()
            )).collect(Collectors.toList());
        };
    }

    @Bean
    @Description("Search for available jobs based on location, skill (or job title), minimum salary, level, and company name.")
    public Function<JobSearchRequest, List<ChatResponse.JobLink>> searchJobs() {
        return request -> {
            System.out.println("AI called searchJobs with: " + request);
            Specification<Job> spec = Specification.where(hasActiveStatus());

            if (request.location() != null && !request.location().isBlank()) {
                spec = spec.and(hasLocation(request.location()));
            }
            if (request.skill() != null && !request.skill().isBlank()) {
                spec = spec.and(hasSkill(request.skill()));
            }
            if (request.minSalary() != null) {
                spec = spec.and(hasMinSalary(request.minSalary()));
            }
            if (request.maxSalary() != null) {
                spec = spec.and(hasMaxSalary(request.maxSalary()));
            }
            if (request.level() != null && !request.level().isBlank()) {
                spec = spec.and(hasLevel(request.level()));
            }
            if (request.companyName() != null && !request.companyName().isBlank()) {
                spec = spec.and(hasCompanyName(request.companyName()));
            }

            Page<Job> jobPage = jobRepository.findAll(spec, PageRequest.of(0, 5));

            List<ChatResponse.JobLink> result = jobPage.getContent().stream().map(job -> ChatResponse.JobLink.builder()
                    .id(job.getId())
                    .title(job.getName())
                    .company(job.getCompany() != null ? job.getCompany().getName() : "Anonymous Company")
                    .url(frontendUrl + "/jobs/" + job.getId())
                    .build()).collect(Collectors.toList());

            JobSearchContext.setJobs(result);
            return result;
        };
    }

    private Specification<Job> hasActiveStatus() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }

    private Specification<Job> hasLocation(String location) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    private Specification<Job> hasSkill(String skill) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Job, Skill> skills = root.join("skills", jakarta.persistence.criteria.JoinType.LEFT);
            return cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + skill.toLowerCase() + "%"),
                    cb.like(cb.lower(skills.get("name")), "%" + skill.toLowerCase() + "%")
            );
        };
    }

    private Specification<Job> hasMinSalary(Double minSalary) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("salary"), minSalary);
    }

    private Specification<Job> hasMaxSalary(Double maxSalary) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("salary"), maxSalary);
    }

    private Specification<Job> hasLevel(String level) {
        return (root, query, cb) -> {
            try {
                vn.hieu.jobhunter.util.constant.LevelEnum levelEnum = vn.hieu.jobhunter.util.constant.LevelEnum.valueOf(level.toUpperCase());
                return cb.equal(root.get("level"), levelEnum);
            } catch (Exception e) {
                return cb.conjunction(); // Return always true if level is invalid
            }
        };
    }

    private Specification<Job> hasCompanyName(String companyName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("company").get("name")), "%" + companyName.toLowerCase() + "%");
    }
}
