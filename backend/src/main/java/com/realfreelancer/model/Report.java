package com.realfreelancer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;
    
    @Column(nullable = false)
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "reported_user_id")
    private Long reportedUserId;
    
    @Column(name = "reported_project_id")
    private Long reportedProjectId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;
    
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolved_by")
    private String resolvedBy;
    
    @Column(columnDefinition = "TEXT")
    private String evidence;
    
    @Column(name = "appeal_reason", columnDefinition = "TEXT")
    private String appealReason;
    
    @Column(name = "appeal_status")
    private String appealStatus;
    
    // Report types
    public enum ReportType {
        SPAM,
        INAPPROPRIATE_CONTENT,
        HARASSMENT,
        FRAUD,
        COPYRIGHT_VIOLATION,
        MISLEADING_INFORMATION,
        OFFENSIVE_LANGUAGE,
        OTHER
    }
    
    // Report status
    public enum ReportStatus {
        PENDING,
        UNDER_REVIEW,
        RESOLVED,
        DISMISSED,
        APPEALED
    }
    
    // Constructors
    public Report() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Report(User reporter, ReportType reportType, String reason) {
        this();
        this.reporter = reporter;
        this.reportType = reportType;
        this.reason = reason;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getReporter() {
        return reporter;
    }
    
    public void setReporter(User reporter) {
        this.reporter = reporter;
    }
    
    public ReportType getReportType() {
        return reportType;
    }
    
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getReportedUserId() {
        return reportedUserId;
    }
    
    public void setReportedUserId(Long reportedUserId) {
        this.reportedUserId = reportedUserId;
    }
    
    public Long getReportedProjectId() {
        return reportedProjectId;
    }
    
    public void setReportedProjectId(Long reportedProjectId) {
        this.reportedProjectId = reportedProjectId;
    }
    
    public ReportStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReportStatus status) {
        this.status = status;
        if (status == ReportStatus.RESOLVED || status == ReportStatus.DISMISSED) {
            this.resolvedAt = LocalDateTime.now();
        }
    }
    
    public String getAdminNotes() {
        return adminNotes;
    }
    
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
    
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    
    public String getResolvedBy() {
        return resolvedBy;
    }
    
    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }
    
    public String getEvidence() {
        return evidence;
    }
    
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
    
    public String getAppealReason() {
        return appealReason;
    }
    
    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }
    
    public String getAppealStatus() {
        return appealStatus;
    }
    
    public void setAppealStatus(String appealStatus) {
        this.appealStatus = appealStatus;
    }
} 