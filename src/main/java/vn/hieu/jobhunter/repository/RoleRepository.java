package vn.hieu.jobhunter.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hieu.jobhunter.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>,
                JpaSpecificationExecutor<Role> {
        boolean existsByName(String name);

        Role findByName(String name);

        List<Role> findAllByName(String name);

        long countById(Long id);

        @Query(value = "SELECT COUNT(*) FROM role_permission rp WHERE rp.role_id = :roleId", nativeQuery = true)
        long countPermissionsByRoleId(@Param("roleId") Long roleId);
}
