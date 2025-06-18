package com.realfreelancer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@EntityListeners(AuditingEntityListener.class)
public class Application {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Pitch is required")
    @Size(min = 20, max = 1000, message = "Pitch must be between 20 and 1000 characters")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String pitch;
    
    @NotNull(message = "Proposed budget is required")
    @Column(name = "proposed_budget", nullable = false)
    private BigDecimal proposedBudget;
    
    @NotNull(message = "Estimated duration is required")
    @Column(name = "estimated_duration_days", nullable = false)
    private Integer estimatedDurationDays;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_id", nullable = false)
    private User freelancer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    @Column(name = "attachment_url")
    private String attachmentUrl;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enum
    public enum ApplicationStatus {
        PENDING, ACCEPTED, REJECTED, WITHDRAWN
    }
    
    // Constructors
    public Application() {}
    
    public Application(String pitch, BigDecimal proposedBudget, Integer estimatedDurationDays, 
                      Project project, User freelancer) {
        this.pitch = pitch;
        this.proposedBudget = proposedBudget;
        this.estimatedDurationDays = estimatedDurationDays;
        this.project = project;
        this.freelancer = freelancer;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPitch() {
        return pitch;
    }
    
    public void setPitch(String pitch) {
        this.pitch = pitch;
    }
    
    public BigDecimal getProposedBudget() {
        return proposedBudget;
    }
    
    public void setProposedBudget(BigDecimal proposedBudget) {
        this.proposedBudget = proposedBudget;
    }
    
    public Integer getEstimatedDurationDays() {
        return estimatedDurationDays;
    }
    
    public void setEstimatedDurationDays(Integer estimatedDurationDays) {
        this.estimatedDurationDays = estimatedDurationDays;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public User getFreelancer() {
        return freelancer;
    }
    
    public void setFreelancer(User freelancer) {
        this.freelancer = freelancer;
    }
    
    public ApplicationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    
    public String getAttachmentUrl() {
        return attachmentUrl;
    }
    
    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public boolean isPending() {
        return this.status == ApplicationStatus.PENDING;
    }
    
    public boolean isAccepted() {
        return this.status == ApplicationStatus.ACCEPTED;
    }
    
    public boolean isRejected() {
        return this.status == ApplicationStatus.REJECTED;
    }
} 