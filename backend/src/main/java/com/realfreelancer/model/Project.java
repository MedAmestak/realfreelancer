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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@EntityListeners(AuditingEntityListener.class)
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    @Column(nullable = false)
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_skills", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "skill")
    private Set<String> requiredSkills = new HashSet<>();
    
    @NotNull(message = "Budget is required")
    @Column(nullable = false)
    private BigDecimal budget;
    
    @NotNull(message = "Deadline is required")
    @Column(nullable = false)
    private LocalDateTime deadline;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status = ProjectStatus.OPEN;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectType type = ProjectType.FREE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freelancer_id")
    private User freelancer;
    
    @Column(name = "attachment_url")
    private String attachmentUrl;
    
    @Column(name = "is_featured")
    private Boolean isFeatured = false;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "application_count")
    private Integer applicationCount = 0;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum ProjectStatus {
        OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    }
    
    public enum ProjectType {
        FREE, PAID
    }
    
    // Constructors
    public Project() {}
    
    public Project(String title, String description, User client) {
        this.title = title;
        this.description = description;
        this.client = client;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Set<String> getRequiredSkills() {
        return requiredSkills;
    }
    
    public void setRequiredSkills(Set<String> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }
    
    public BigDecimal getBudget() {
        return budget;
    }
    
    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
    
    public LocalDateTime getDeadline() {
        return deadline;
    }
    
    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
    
    public ProjectStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
    
    public ProjectType getType() {
        return type;
    }
    
    public void setType(ProjectType type) {
        this.type = type;
    }
    
    public User getClient() {
        return client;
    }
    
    public void setClient(User client) {
        this.client = client;
    }
    
    public User getFreelancer() {
        return freelancer;
    }
    
    public void setFreelancer(User freelancer) {
        this.freelancer = freelancer;
    }
    
    public String getAttachmentUrl() {
        return attachmentUrl;
    }
    
    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public Integer getApplicationCount() {
        return applicationCount;
    }
    
    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
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
    public void addRequiredSkill(String skill) {
        this.requiredSkills.add(skill);
    }
    
    public void removeRequiredSkill(String skill) {
        this.requiredSkills.remove(skill);
    }
    
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    public void incrementApplicationCount() {
        this.applicationCount++;
    }
    
    public boolean isOpen() {
        return this.status == ProjectStatus.OPEN;
    }
    
    public boolean isCompleted() {
        return this.status == ProjectStatus.COMPLETED;
    }
} 