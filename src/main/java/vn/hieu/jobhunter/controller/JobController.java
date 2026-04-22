package vn.hieu.jobhunter.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hieu.jobhunter.domain.Job;
import vn.hieu.jobhunter.domain.User;
import vn.hieu.jobhunter.domain.UserSubscription;
import vn.hieu.jobhunter.domain.response.ResultPaginationDTO;
import vn.hieu.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hieu.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hieu.jobhunter.repository.UserSubscriptionRepository;
import vn.hieu.jobhunter.service.JobService;
import vn.hieu.jobhunter.service.RoleService;
import vn.hieu.jobhunter.service.UserService;
import vn.hieu.jobhunter.util.annotation.ApiMessage;
import vn.hieu.jobhunter.util.error.IdInvalidException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;
    private final UserService userService;
    private final RoleService roleService;
    private final UserSubscriptionRepository userSubscriptionRepository;

    public JobController(JobService jobService, UserService userService, RoleService roleService,
            UserSubscriptionRepository userSubscriptionRepository) {
        this.jobService = jobService;
        this.userService = userService;
        this.roleService = roleService;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job)
            throws IdInvalidException, vn.hieu.jobhunter.util.error.PostLimitExceededException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.handleGetUserByUsername(username);

        if (user == null || !"HR".equals(user.getRole().getName())) {
            throw new IdInvalidException("Access Denied: Only HR can create jobs.");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(job.getId());
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }

        return ResponseEntity.ok()
                .body(this.jobService.update(job, currentJob.get()));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }
        this.jobService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job by id")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.fetchJobById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Job not found");
        }

        return ResponseEntity.ok().body(currentJob.get());
    }

    @GetMapping("/jobs")
    @ApiMessage("Get jobs with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.handleGetUserByUsername(username);
        long idRole = user.getRole().getId();

        ResultPaginationDTO result;

        boolean permissionVsRole = roleService.permissionVsRole(idRole);

        if (permissionVsRole) {
            result = this.jobService.fetchAll(spec, pageable);
        } else {
            if (user.getCompany() != null) {
                result = this.jobService.fetchJobsByCompany(user.getCompany().getId(), pageable);
            } else {
                result = this.jobService.fetchJobsByCreatedBy(username, pageable);
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/jobs/all")
    @ApiMessage("Get all jobs (ignore role check)")
    public ResponseEntity<ResultPaginationDTO> getAllJobsUnfiltered(
            @Filter Specification<Job> spec,
            Pageable pageable) {
        ResultPaginationDTO result = this.jobService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/jobs/by-created/{username}")
    @ApiMessage("Get jobs by createdBy")
    public ResponseEntity<ResultPaginationDTO> getJobsByCreatedBy(
            @PathVariable("username") String username,
            Pageable pageable) {

        return ResponseEntity.ok()
                .body(this.jobService.fetchJobsByCreatedBy(username, pageable));
    }

    @GetMapping("/jobs/count-by-company")
    @ApiMessage("Get job count for the current user's company")
    public ResponseEntity<Long> getJobCountByCompany() throws IdInvalidException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.handleGetUserByUsername(username);

        if (user == null || user.getCompany() == null) {
            return ResponseEntity.ok(0L);
        }

        return ResponseEntity.ok(this.jobService.countJobsByCompany(user.getCompany()));
    }

    @GetMapping("/jobs/search")
    @ApiMessage("Search jobs by name, skills, location, level and salary")
    public ResponseEntity<ResultPaginationDTO> searchJobs(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "minSalary", required = false) Double minSalary,
            @RequestParam(value = "skills", required = false) List<Long> skillIds,
            Pageable pageable) {

        Specification<Job> spec = vn.hieu.jobhunter.repository.JobSpecification.filterJob(name, location, skillIds, level, minSalary);
        ResultPaginationDTO result = this.jobService.fetchAll(spec, pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/jobs/posting-stats")
    @ApiMessage("Get posting statistics for current HR user")
    public ResponseEntity<Map<String, Object>> getPostingStats() throws IdInvalidException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.handleGetUserByUsername(username);

        if (user == null) {
            throw new IdInvalidException("User không tồn tại.");
        }

        Map<String, Object> stats = new HashMap<>();

        long usedPosts = 0;
        long remainingPosts = 0;
        String packageName = "Free (Miễn phí)";

        // Count current jobs by company
        if (user.getCompany() != null) {
            usedPosts = this.jobService.countJobsByCompany(user.getCompany());
        }

        // Check active subscription
        Optional<UserSubscription> activeSub = this.userSubscriptionRepository
                .findFirstByUserAndActiveTrueAndEndDateAfterOrderByEndDateAsc(user, Instant.now());

        if (activeSub.isPresent()) {
            UserSubscription sub = activeSub.get();
            packageName = sub.getSubscription().getName();
            if (sub.getSubscription().getLimitPosts() == -1) {
                remainingPosts = -1; // -1 means unlimited
            } else {
                remainingPosts = sub.getTotalPosts() - sub.getUsedPosts();
                if (remainingPosts < 0)
                    remainingPosts = 0;
            }
        } else {
            // Free tier: max 2 posts
            if (usedPosts < 2) {
                remainingPosts = 2 - usedPosts;
            } else {
                remainingPosts = 0;
            }
        }

        stats.put("usedPosts", usedPosts);
        stats.put("remainingPosts", remainingPosts);
        stats.put("packageName", packageName);

        return ResponseEntity.ok(stats);
    }
}
