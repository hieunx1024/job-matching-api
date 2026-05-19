package vn.hieu.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;
import vn.hieu.jobhunter.domain.Job;
import vn.hieu.jobhunter.domain.Skill;

public class JobSpecification {

    public static Specification<Job> filterJob(String keyword, String location, List<Long> skillIds, String level, Double minSalary) {
        return (root, query, cb) -> {
            // XÓA query.distinct(true) vì nó gây lỗi Postgres khi ORDER BY theo biểu thức không có trong SELECT

            // Điều kiện bắt buộc: Tin tuyển dụng phải còn hoạt động
            Predicate predicate = cb.equal(root.get("active"), true);
            
            // Điều kiện "Khớp bất kỳ" (OR) để không bỏ sót các kết quả gần đúng
            Predicate matchAny = cb.disjunction();
            boolean hasCriteria = false;

            // Biểu thức tính tổng điểm (Score)
            Expression<Integer> score = cb.literal(0);

            // 1. Khớp Từ khóa/Tên công việc (Trọng số cao nhất: 10 điểm)
            if (keyword != null && !keyword.isEmpty()) {
                Predicate p = cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
                matchAny = cb.or(matchAny, p);
                hasCriteria = true;
                
                score = cb.sum(score, cb.<Integer>selectCase().when(p, 10).otherwise(0));
            }

            // 2. Khớp Địa điểm (Trọng số: 5 điểm)
            if (location != null && !location.isEmpty()) {
                Predicate p = cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
                matchAny = cb.or(matchAny, p);
                hasCriteria = true;

                score = cb.sum(score, cb.<Integer>selectCase().when(p, 5).otherwise(0));
            }

            // 3. Khớp Cấp bậc (Trọng số: 5 điểm)
            if (level != null && !level.isEmpty()) {
                try {
                    vn.hieu.jobhunter.util.constant.LevelEnum levelEnum = vn.hieu.jobhunter.util.constant.LevelEnum.valueOf(level.toUpperCase());
                    Predicate p = cb.equal(root.get("level"), levelEnum);
                    matchAny = cb.or(matchAny, p);
                    hasCriteria = true;

                    score = cb.sum(score, cb.<Integer>selectCase().when(p, 5).otherwise(0));
                } catch (IllegalArgumentException e) {
                    // Bỏ qua nếu enum không hợp lệ
                }
            }

            // 4. Khớp Mức lương (Lớn hơn hoặc bằng) (Trọng số: 5 điểm)
            if (minSalary != null) {
                Predicate p = cb.greaterThanOrEqualTo(root.get("salary"), minSalary);
                matchAny = cb.or(matchAny, p);
                hasCriteria = true;

                score = cb.sum(score, cb.<Integer>selectCase().when(p, 5).otherwise(0));
            }

            // 5. Khớp Kỹ năng (Trọng số: 5 điểm nếu khớp ít nhất 1 kỹ năng)
            // Dùng Subquery thay vì Join để tránh lặp bản ghi và không cần dùng DISTINCT
            if (skillIds != null && !skillIds.isEmpty()) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Job> subRoot = subquery.from(Job.class);
                Join<Job, Skill> subJoin = subRoot.join("skills");
                
                subquery.select(cb.literal(1L));
                subquery.where(
                    cb.and(
                        cb.equal(subRoot.get("id"), root.get("id")),
                        subJoin.get("id").in(skillIds)
                    )
                );
                
                Predicate p = cb.exists(subquery);
                matchAny = cb.or(matchAny, p);
                hasCriteria = true;

                score = cb.sum(score, cb.<Integer>selectCase().when(p, 5).otherwise(0));
            }

            // Nếu người dùng có nhập ít nhất 1 tiêu chí, áp dụng điều kiện OR
            if (hasCriteria) {
                predicate = cb.and(predicate, matchAny);
            }

            // SẮP XẾP THEO ĐIỂM GIẢM DẦN (Liên quan nhất lên đầu)
            query.orderBy(cb.desc(score));

            return predicate;
        };
    }
}
