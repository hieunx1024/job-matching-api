package vn.hieu.jobhunter.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hieu.jobhunter.domain.UserCv;

@Repository
public interface UserCvRepository extends JpaRepository<UserCv, Long>, JpaSpecificationExecutor<UserCv> {
    List<UserCv> findByUserId(long userId);
}
