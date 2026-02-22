package vn.hieu.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import vn.hieu.jobhunter.domain.Job;
import vn.hieu.jobhunter.domain.Skill;

public class JobSpecification {

    public static Specification<Job> filterJob(String keyword, String location, List<Long> skillIds) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Filter by Name (LIKE)
            if (keyword != null && !keyword.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("name"), "%" + keyword + "%"));
            }

            // Filter by Location (LIKE)
            if (location != null && !location.isEmpty()) {
                predicate = cb.and(predicate, cb.like(root.get("location"), "%" + location + "%"));
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
