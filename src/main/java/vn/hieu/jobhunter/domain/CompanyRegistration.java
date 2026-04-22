package vn.hieu.jobhunter.domain;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.hieu.jobhunter.util.SecurityUtil;
import vn.hieu.jobhunter.util.constant.RegistrationStatus;

@Entity
@Table(name = "company_registrations")
@Getter
@Setter
public class CompanyRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 👤 Mỗi CompanyRegistration chỉ thuộc về 1 user
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String companyName;
    @Column(columnDefinition = "TEXT")
    private String description;
    private String address;
    private String logo;
    private String facebookLink;
    private String githubLink;
    private String verificationDocument;
    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
        this.updatedAt = Instant.now();
    }
}
