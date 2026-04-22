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

import jakarta.persistence.criteria.Join;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class JobSearchFunction {

    private final JobRepository jobRepository;

    public record JobSearchRequest(String location, String skill, Double minSalary, String level) {
    }

    @Bean
    @Description("Tìm kiếm các công việc có sẵn trong hệ thống dựa trên địa điểm, kỹ năng (hoặc tên công việc), mức lương tối thiểu, và cấp bậc (INTERN, FRESHER, JUNIOR, MIDDLE, SENIOR)")
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
            if (request.level() != null && !request.level().isBlank()) {
                spec = spec.and(hasLevel(request.level()));
            }

            Page<Job> jobPage = jobRepository.findAll(spec, PageRequest.of(0, 5));

            List<ChatResponse.JobLink> result = jobPage.getContent().stream().map(job -> ChatResponse.JobLink.builder()
                    .id(job.getId())
                    .title(job.getName())
                    .company(job.getCompany() != null ? job.getCompany().getName() : "Công ty ẩn danh")
                    .url("http://localhost:5173/jobs/" + job.getId())
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
}
