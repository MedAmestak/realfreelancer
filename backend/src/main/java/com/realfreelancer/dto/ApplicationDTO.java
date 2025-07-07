package com.realfreelancer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ApplicationDTO {
    private Long id;
    private String pitch;
    private BigDecimal proposedBudget;
    private Integer estimatedDurationDays;
    private String status;
    private String attachmentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
    private String projectTitle;
    private Long freelancerId;
    private String freelancerUsername;

    public ApplicationDTO() {}

    public ApplicationDTO(Long id, String pitch, BigDecimal proposedBudget, Integer estimatedDurationDays, String status, String attachmentUrl, LocalDateTime createdAt, LocalDateTime updatedAt, Long projectId, String projectTitle, Long freelancerId, String freelancerUsername) {
        this.id = id;
        this.pitch = pitch;
        this.proposedBudget = proposedBudget;
        this.estimatedDurationDays = estimatedDurationDays;
        this.status = status;
        this.attachmentUrl = attachmentUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.freelancerId = freelancerId;
        this.freelancerUsername = freelancerUsername;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPitch() { return pitch; }
    public void setPitch(String pitch) { this.pitch = pitch; }
    public BigDecimal getProposedBudget() { return proposedBudget; }
    public void setProposedBudget(BigDecimal proposedBudget) { this.proposedBudget = proposedBudget; }
    public Integer getEstimatedDurationDays() { return estimatedDurationDays; }
    public void setEstimatedDurationDays(Integer estimatedDurationDays) { this.estimatedDurationDays = estimatedDurationDays; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
    public Long getFreelancerId() { return freelancerId; }
    public void setFreelancerId(Long freelancerId) { this.freelancerId = freelancerId; }
    public String getFreelancerUsername() { return freelancerUsername; }
    public void setFreelancerUsername(String freelancerUsername) { this.freelancerUsername = freelancerUsername; }
} 