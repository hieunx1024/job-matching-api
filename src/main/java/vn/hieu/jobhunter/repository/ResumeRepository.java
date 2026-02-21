package vn.hieu.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hieu.jobhunter.domain.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>,
        JpaSpecificationExecutor<Resume> {

    long countByEmail(String email);

    long countByEmailAndStatus(String email, vn.hieu.jobhunter.util.constant.ResumeStateEnum status);
}
