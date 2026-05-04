package vn.hieu.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import vn.hieu.jobhunter.domain.Job;
import vn.hieu.jobhunter.domain.Skill;

public class JobSpecification {

    public static Specification<Job> filterJob(String keyword, String location, List<Long> skillIds, String level, Double minSalary) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Filter by Name (LIKE case-insensitive)
            if (keyword != null && !keyword.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }

            // Filter by Location (LIKE case-insensitive)
            if (location != null && !location.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }

            // Filter by Level (Exact)
            if (level != null && !level.isEmpty()) {
                try {
                    vn.hieu.jobhunter.util.constant.LevelEnum levelEnum = vn.hieu.jobhunter.util.constant.LevelEnum.valueOf(level);
                    predicate = cb.and(predicate, cb.equal(root.get("level"), levelEnum));
                } catch (IllegalArgumentException e) {
                    // Invalid enum value, ignore
                }
            }

            // Filter by Salary (>= minSalary)
            if (minSalary != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("salary"), minSalary));
            }

            // Filter by Skills (IN)
            if (skillIds != null && !skillIds.isEmpty()) {
                Join<Job, Skill> join = root.join("skills");
                predicate = cb.and(predicate, join.get("id").in(skillIds));
                query.distinct(true); // Avoid duplicate jobs if multiple skills match
            }

            // Always filter by Active jobs for search
            predicate = cb.and(predicate, cb.equal(root.get("active"), true));

            return predicate;
        };
    }
}
