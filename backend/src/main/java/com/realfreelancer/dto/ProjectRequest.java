package com.realfreelancer.dto;

import com.realfreelancer.model.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

public class ProjectRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;
    
    private Set<String> requiredSkills;
    
    @NotNull(message = "Budget is required")
    private BigDecimal budget;
    
    @NotNull(message = "Deadline is required")
    private LocalDateTime deadline;
    
    private Project.ProjectType type = Project.ProjectType.FREE;
    
    private String attachmentUrl;
    
    // Constructors
    public ProjectRequest() {}
    
    public ProjectRequest(String title, String description, Set<String> requiredSkills, 
                         BigDecimal budget, LocalDateTime deadline) {
        this.title = title;
        this.description = description;
        this.requiredSkills = (requiredSkills != null) ? new HashSet<>(requiredSkills) : null;
        this.budget = budget;
        this.deadline = deadline;
    }
    
    // Getters and Setters
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
        this.requiredSkills = (requiredSkills != null) ? new HashSet<>(requiredSkills) : null;
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
    
    public Project.ProjectType getType() {
        return type;
    }
    
    public void setType(Project.ProjectType type) {
        this.type = type;
    }
    
    public String getAttachmentUrl() {
        return attachmentUrl;
    }
    
    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
} 