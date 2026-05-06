package vn.hieu.jobhunter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hieu.jobhunter.domain.Job;
import vn.hieu.jobhunter.domain.Skill;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>,
                JpaSpecificationExecutor<Job> {

        List<Job> findBySkillsIn(List<Skill> skills);

        long countByCompany(vn.hieu.jobhunter.domain.Company company);

        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.data.jpa.repository.Query("UPDATE Job j SET j.active = false WHERE j.active = true AND j.endDate < :now")
        int deactivateExpiredJobs(java.time.Instant now);
}
