package vn.hieu.jobhunter.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.hieu.jobhunter.domain.Permission;
import vn.hieu.jobhunter.domain.Role;
import vn.hieu.jobhunter.domain.Skill;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.repository.PermissionRepository;
import vn.hieu.jobhunter.repository.RoleRepository;
import vn.hieu.jobhunter.repository.SkillRepository;
import vn.hieu.jobhunter.repository.UserRepository;
import vn.hieu.jobhunter.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {

        private final PermissionRepository permissionRepository;
        private final RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final SkillRepository skillRepository;
        private final vn.hieu.jobhunter.repository.SubscriptionRepository subscriptionRepository;
        private final PasswordEncoder passwordEncoder;

        public DatabaseInitializer(
                        PermissionRepository permissionRepository,
                        RoleRepository roleRepository,
                        UserRepository userRepository,
                        SkillRepository skillRepository,
                        vn.hieu.jobhunter.repository.SubscriptionRepository subscriptionRepository,
                        PasswordEncoder passwordEncoder) {
                this.permissionRepository = permissionRepository;
                this.roleRepository = roleRepository;
                this.userRepository = userRepository;
                this.skillRepository = skillRepository;
                this.subscriptionRepository = subscriptionRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void run(String... args) throws Exception {
                System.out.println(">>> START INIT DATABASE");
                long countPermissions = this.permissionRepository.count();
                long countRoles = this.roleRepository.count();
                long countUsers = this.userRepository.count();
                long countSkills = this.skillRepository.count();

                if (countPermissions == 0) {
                        ArrayList<Permission> arr = new ArrayList<>();

                        // === CÔNG TY ===
                        arr.add(new Permission("Tạo công ty", "/api/v1/companies", "POST", "COMPANIES"));
                        arr.add(new Permission("Cập nhật công ty", "/api/v1/companies", "PUT", "COMPANIES"));
                        arr.add(new Permission("Xóa công ty", "/api/v1/companies/{id}", "DELETE", "COMPANIES"));
                        arr.add(new Permission("Lấy thông tin công ty theo ID", "/api/v1/companies/{id}", "GET",
                                        "COMPANIES"));
                        arr.add(new Permission("Lấy danh sách công ty (phân trang)", "/api/v1/companies", "GET",
                                        "COMPANIES"));

                        // === CÔNG VIỆC ===
                        arr.add(new Permission("Tạo công việc", "/api/v1/jobs", "POST", "JOBS"));
                        arr.add(new Permission("Cập nhật công việc", "/api/v1/jobs", "PUT", "JOBS"));
                        arr.add(new Permission("Xóa công việc", "/api/v1/jobs/{id}", "DELETE", "JOBS"));
                        arr.add(new Permission("Lấy thông tin công việc theo ID", "/api/v1/jobs/{id}", "GET", "JOBS"));
                        arr.add(new Permission("Lấy danh sách công việc (phân trang)", "/api/v1/jobs", "GET", "JOBS"));
                        arr.add(new Permission("Lấy công việc theo người tạo", "/api/v1/jobs/by-created/{username}",
                                        "GET",
                                        "JOBS"));

                        // === QUYỀN (PERMISSION) ===
                        arr.add(new Permission("Tạo quyền", "/api/v1/permissions", "POST", "PERMISSIONS"));
                        arr.add(new Permission("Cập nhật quyền", "/api/v1/permissions", "PUT", "PERMISSIONS"));
                        arr.add(new Permission("Xóa quyền", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
                        arr.add(new Permission("Lấy quyền theo ID", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
                        arr.add(new Permission("Lấy danh sách quyền (phân trang)", "/api/v1/permissions", "GET",
                                        "PERMISSIONS"));

                        // === HỒ SƠ (RESUME) ===
                        arr.add(new Permission("Tạo hồ sơ", "/api/v1/resumes", "POST", "RESUMES"));
                        arr.add(new Permission("Cập nhật hồ sơ", "/api/v1/resumes", "PUT", "RESUMES"));
                        arr.add(new Permission("Xóa hồ sơ", "/api/v1/resumes/{id}", "DELETE", "RESUMES"));
                        arr.add(new Permission("Lấy hồ sơ theo ID", "/api/v1/resumes/{id}", "GET", "RESUMES"));
                        arr.add(new Permission("Lấy danh sách hồ sơ (phân trang)", "/api/v1/resumes", "GET",
                                        "RESUMES"));

                        // === VAI TRÒ (ROLE) ===
                        arr.add(new Permission("Tạo vai trò", "/api/v1/roles", "POST", "ROLES"));
                        arr.add(new Permission("Cập nhật vai trò", "/api/v1/roles", "PUT", "ROLES"));
                        arr.add(new Permission("Xóa vai trò", "/api/v1/roles/{id}", "DELETE", "ROLES"));
                        arr.add(new Permission("Lấy vai trò theo ID", "/api/v1/roles/{id}", "GET", "ROLES"));
                        arr.add(new Permission("Lấy danh sách vai trò (phân trang)", "/api/v1/roles", "GET", "ROLES"));

                        // === NGƯỜI DÙNG (USER) ===
                        arr.add(new Permission("Tạo người dùng", "/api/v1/users", "POST", "USERS"));
                        arr.add(new Permission("Cập nhật người dùng", "/api/v1/users", "PUT", "USERS"));
                        arr.add(new Permission("Xóa người dùng", "/api/v1/users/{id}", "DELETE", "USERS"));
                        arr.add(new Permission("Lấy người dùng theo ID", "/api/v1/users/{id}", "GET", "USERS"));
                        arr.add(new Permission("Lấy danh sách người dùng (phân trang)", "/api/v1/users", "GET",
                                        "USERS"));

                        // === NGƯỜI ĐĂNG KÝ (SUBSCRIBER) ===
                        arr.add(new Permission("Tạo người đăng ký", "/api/v1/subscribers", "POST", "SUBSCRIBERS"));
                        arr.add(new Permission("Cập nhật người đăng ký", "/api/v1/subscribers", "PUT", "SUBSCRIBERS"));
                        arr.add(new Permission("Xóa người đăng ký", "/api/v1/subscribers/{id}", "DELETE",
                                        "SUBSCRIBERS"));
                        arr.add(new Permission("Lấy người đăng ký theo ID", "/api/v1/subscribers/{id}", "GET",
                                        "SUBSCRIBERS"));
                        arr.add(new Permission("Lấy danh sách người đăng ký (phân trang)", "/api/v1/subscribers", "GET",
                                        "SUBSCRIBERS"));

                        // === TỆP TIN (FILE) ===
                        arr.add(new Permission("Tải xuống tệp", "/api/v1/files", "POST", "FILES"));
                        arr.add(new Permission("Tải lên tệp", "/api/v1/files", "GET", "FILES"));

                        // === ĐĂNG KÝ CÔNG TY (COMPANY REGISTRATION) ===
                        arr.add(new Permission("Tạo yêu cầu đăng ký công ty", "/api/v1/company-registrations", "POST",
                                        "COMPANY_REGISTRATIONS"));
                        arr.add(new Permission("Xem danh sách yêu cầu đăng ký công ty", "/api/v1/company-registrations",
                                        "GET",
                                        "COMPANY_REGISTRATIONS"));
                        arr.add(new Permission("Cập nhật trạng thái đăng ký công ty",
                                        "/api/v1/company-registrations/{id}/status",
                                        "PUT", "COMPANY_REGISTRATIONS"));
                        arr.add(new Permission("Lấy thông tin đăng ký công ty theo ID",
                                        "/api/v1/company-registrations/{id}", "GET",
                                        "COMPANY_REGISTRATIONS"));
                        arr.add(new Permission("Từ chối đăng ký công ty", "/api/v1/company-registrations/{id}/reject",
                                        "PUT",
                                        "COMPANY_REGISTRATIONS"));
                        arr.add(new Permission("Xóa yêu cầu đăng ký công ty", "/api/v1/company-registrations/{id}",
                                        "DELETE",
                                        "COMPANY_REGISTRATIONS"));

                        // === TIN NHẮN (MESSAGE) ===
                        arr.add(new Permission("Gửi tin nhắn", "/api/v1/messages", "POST", "MESSAGES"));
                        arr.add(new Permission("Xóa tin nhắn", "/api/v1/messages/{id}", "DELETE", "MESSAGES"));
                        arr.add(new Permission("Lấy tin nhắn theo ID", "/api/v1/messages/{id}", "GET", "MESSAGES"));
                        arr.add(new Permission("Lấy danh sách tin nhắn giữa 2 người dùng",
                                        "/api/v1/messages/conversation", "GET",
                                        "MESSAGES"));

                        this.permissionRepository.saveAll(arr);
                }

                if (countRoles >= 0) {
                        List<Permission> allPermissions = this.permissionRepository.findAll();

                        // === ROLE SUPER_ADMIN ===
                        List<Role> adminRoles = this.roleRepository.findAllByName("SUPER_ADMIN");
                        if (adminRoles == null || adminRoles.isEmpty()) {
                                Role adminRole = new Role();
                                adminRole.setName("SUPER_ADMIN");
                                adminRole.setDescription("Quản trị viên có toàn quyền trong hệ thống");
                                adminRole.setActive(true);
                                adminRole.setPermissions(allPermissions);
                                this.roleRepository.save(adminRole);
                        } else if (adminRoles.size() > 1) {
                                // Cleanup duplicates, keep the first one
                                for (int i = 1; i < adminRoles.size(); i++) {
                                        this.roleRepository.delete(adminRoles.get(i));
                                }
                                System.out.println(">>> CLEANED UP " + (adminRoles.size() - 1)
                                                + " DUPLICATE SUPER_ADMIN ROLES");
                        }

                        // === ROLE MANAGER ===
                        Role managerRole = this.roleRepository.findByName("MANAGER");
                        if (managerRole == null) {
                                List<Permission> managerPermissions = allPermissions.stream()
                                                .filter(p -> p.getModule().equalsIgnoreCase("USERS")
                                                                || p.getModule().equalsIgnoreCase("COMPANIES")
                                                                || p.getModule().equalsIgnoreCase("RESUMES"))
                                                .toList();

                                managerRole = new Role();
                                managerRole.setName("MANAGER");
                                managerRole.setDescription("Người quản lý có quyền với người dùng, công ty và hồ sơ");
                                managerRole.setActive(true);
                                managerRole.setPermissions(managerPermissions);

                                this.roleRepository.save(managerRole);
                                System.out.println(">>> Đã tạo ROLE: MANAGER với quyền USERS, COMPANIES, RESUMES");
                        }

                        // === ROLE HR ===
                        Role hrRole = this.roleRepository.findByName("HR");
                        if (hrRole == null) {
                                // HR Permissions: Jobs (ALL), Resumes (ALL), Company (ALL), Files (ALL)
                                // Note: In real app, HR only sees THEIR company/jobs. This is handled in
                                // Controller/Service logic.
                                List<Permission> hrPermissions = allPermissions.stream()
                                                .filter(p -> p.getModule().equalsIgnoreCase("JOBS")
                                                                || p.getModule().equalsIgnoreCase("RESUMES")
                                                                || p.getModule().equalsIgnoreCase("COMPANIES")
                                                                || p.getModule().equalsIgnoreCase("FILES")
                                                                || p.getModule().equalsIgnoreCase(
                                                                                "COMPANY_REGISTRATIONS"))
                                                .toList();

                                hrRole = new Role();
                                hrRole.setName("HR");
                                hrRole.setDescription("Nhà tuyển dụng: Đăng tin, quản lý hồ sơ");
                                hrRole.setActive(true);
                                hrRole.setPermissions(hrPermissions);
                                this.roleRepository.save(hrRole);
                        }

                        // === ROLE CANDIDATE ===
                        Role candidateRole = this.roleRepository.findByName("CANDIDATE");
                        if (candidateRole == null) {
                                // Candidate Permissions: Jobs (VIEW), Resumes (CREATE, VIEW), Files (Upload)
                                List<Permission> candidatePermissions = allPermissions.stream()
                                                .filter(p -> (p.getModule().equalsIgnoreCase("JOBS")
                                                                && p.getMethod().equals("GET"))
                                                                || (p.getModule().equalsIgnoreCase("RESUMES") && (p
                                                                                .getMethod().equals("POST")
                                                                                || p.getMethod().equals("GET")))
                                                                || (p.getModule().equalsIgnoreCase("FILES")))
                                                .toList();

                                candidateRole = new Role();
                                candidateRole.setName("CANDIDATE");
                                candidateRole.setDescription("Ứng viên: Tìm việc, nộp hồ sơ");
                                candidateRole.setActive(true);
                                candidateRole.setPermissions(candidatePermissions);
                                this.roleRepository.save(candidateRole);
                        }
                }

                if (countUsers == 0) {
                        // === ADMIN USER ===
                        User adminUser = new User();
                        adminUser.setEmail("admin@gmail.com");
                        adminUser.setAddress("Hà Nội");
                        adminUser.setAge(25);
                        adminUser.setGender(GenderEnum.MALE);
                        adminUser.setName("Super Admin");
                        adminUser.setPassword(this.passwordEncoder.encode("123456"));

                        Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
                        if (adminRole != null) {
                                adminUser.setRole(adminRole);
                        }
                        this.userRepository.save(adminUser);

                        // // // === MANAGER USER ===
                        // // User managerUser = new User();
                        // // managerUser.setEmail("manager@gmail.com");
                        // // managerUser.setAddress("Hồ Chí Minh");
                        // // managerUser.setAge(30);
                        // // managerUser.setGender(GenderEnum.FEMALE);
                        // // managerUser.setName("Manager");
                        // // managerUser.setPassword(this.passwordEncoder.encode("123456"));

                        // // Role managerRole = this.roleRepository.findByName("MANAGER");
                        // // if (managerRole != null) {
                        // // managerUser.setRole(managerRole);
                        // // }
                        // this.userRepository.save(managerUser);
                }

                // === INIT SUBSCRIPTIONS ===
                long countSubscriptions = this.subscriptionRepository.count();
                if (countSubscriptions == 0) {
                        List<vn.hieu.jobhunter.domain.Subscription> subscriptions = new ArrayList<>();

                        vn.hieu.jobhunter.domain.Subscription free = new vn.hieu.jobhunter.domain.Subscription();
                        free.setName("Free");
                        free.setPrice(0);
                        free.setDurationDays(30);
                        free.setLimitPosts(3);
                        subscriptions.add(free);

                        vn.hieu.jobhunter.domain.Subscription pro = new vn.hieu.jobhunter.domain.Subscription();
                        pro.setName("Professional");
                        pro.setPrice(500000);
                        pro.setDurationDays(30);
                        pro.setLimitPosts(20);
                        subscriptions.add(pro);

                        vn.hieu.jobhunter.domain.Subscription ent = new vn.hieu.jobhunter.domain.Subscription();
                        ent.setName("Enterprise");
                        ent.setPrice(2000000);
                        ent.setDurationDays(30);
                        ent.setLimitPosts(-1); // Không giới hạn
                        subscriptions.add(ent);

                        for (vn.hieu.jobhunter.domain.Subscription sub : subscriptions) {
                                sub.setCreatedAt(java.time.Instant.now());
                                sub.setUpdatedAt(java.time.Instant.now());
                                sub.setCreatedBy("system");
                                sub.setUpdatedBy("system");
                                this.subscriptionRepository.save(sub);
                        }
                        System.out.println(">>> Đã khởi tạo " + subscriptions.size() + " subscriptions");
                }

                // === INIT SKILLS ===
                if (countSkills == 0) {
                        List<Skill> skills = new ArrayList<>();

                        // Programming Languages
                        skills.add(new Skill("Java"));
                        skills.add(new Skill("Python"));
                        skills.add(new Skill("JavaScript"));
                        skills.add(new Skill("TypeScript"));
                        skills.add(new Skill("C++"));
                        skills.add(new Skill("C#"));
                        skills.add(new Skill("PHP"));
                        skills.add(new Skill("Ruby"));
                        skills.add(new Skill("Go"));
                        skills.add(new Skill("Kotlin"));
                        skills.add(new Skill("Swift"));

                        // Frontend Frameworks
                        skills.add(new Skill("React"));
                        skills.add(new Skill("Angular"));
                        skills.add(new Skill("Vue.js"));
                        skills.add(new Skill("Next.js"));
                        skills.add(new Skill("Nuxt.js"));

                        // Backend Frameworks
                        skills.add(new Skill("Spring Boot"));
                        skills.add(new Skill("Node.js"));
                        skills.add(new Skill("Express.js"));
                        skills.add(new Skill("Django"));
                        skills.add(new Skill("Flask"));
                        skills.add(new Skill("Laravel"));
                        skills.add(new Skill("ASP.NET"));

                        // Databases
                        skills.add(new Skill("MySQL"));
                        skills.add(new Skill("PostgreSQL"));
                        skills.add(new Skill("MongoDB"));
                        skills.add(new Skill("Redis"));
                        skills.add(new Skill("Oracle"));
                        skills.add(new Skill("SQL Server"));

                        // DevOps & Cloud
                        skills.add(new Skill("Docker"));
                        skills.add(new Skill("Kubernetes"));
                        skills.add(new Skill("AWS"));
                        skills.add(new Skill("Azure"));
                        skills.add(new Skill("Google Cloud"));
                        skills.add(new Skill("Jenkins"));
                        skills.add(new Skill("GitLab CI/CD"));

                        // Mobile Development
                        skills.add(new Skill("React Native"));
                        skills.add(new Skill("Flutter"));
                        skills.add(new Skill("Android"));
                        skills.add(new Skill("iOS"));

                        // Other Skills
                        skills.add(new Skill("Git"));
                        skills.add(new Skill("REST API"));
                        skills.add(new Skill("GraphQL"));
                        skills.add(new Skill("Microservices"));
                        skills.add(new Skill("Agile"));
                        skills.add(new Skill("Scrum"));
                        skills.add(new Skill("UI/UX Design"));
                        skills.add(new Skill("HTML/CSS"));
                        skills.add(new Skill("Tailwind CSS"));
                        skills.add(new Skill("Bootstrap"));
                        skills.add(new Skill("Testing"));
                        skills.add(new Skill("JUnit"));
                        skills.add(new Skill("Jest"));
                        skills.add(new Skill("Selenium"));

                        this.skillRepository.saveAll(skills);
                        System.out.println(">>> Đã khởi tạo " + skills.size() + " skills");
                }

                if (countPermissions > 0 && countRoles > 0 && countUsers > 0 && countSkills > 0) {
                        System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
                } else {
                        System.out.println(">>> END INIT DATABASE");
                }
        }
}
