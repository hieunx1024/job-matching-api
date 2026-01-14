package vn.hieu.jobhunter.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.hieu.jobhunter.domain.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>,
                JpaSpecificationExecutor<Permission> {

        boolean existsByModuleAndApiPathAndMethod(String module, String apiPath, String method);

        List<Permission> findByRoles_Id(Long roleId);

        List<Permission> findByIdIn(List<Long> id);

        long count();

        // 🔹 Lấy tất cả quyền theo module (vd: "JOBS", "USERS", "RESUMES")
        List<Permission> findByModule(String module);

        // 🔹 Lấy một quyền cụ thể theo apiPath và method (vd: "/api/v1/companies",
        // "PUT")
        Permission findByApiPathAndMethod(String apiPath, String method);
}
