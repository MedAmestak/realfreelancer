package com.realfreelancer.dto;

import com.realfreelancer.model.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

public class ProjectRequest {
    
    @NotBlank(message = "Title is required") // min 5 chars
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required") // min 20 chars
    @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
    private String description;
    
    private Set<String> requiredSkills = new HashSet<>();
    
    @NotNull(message = "Budget is required")
    private BigDecimal budget;
    
    @NotNull(message = "Deadline is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS][.SS][.S][XXX][X]", shape = JsonFormat.Shape.STRING)
    private LocalDateTime deadline;
    
    private String type = "FREE";
    
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
        this.requiredSkills = (requiredSkills != null) ? new HashSet<>(requiredSkills) : new HashSet<>();
    }
    
    public BigDecimal getBudget() {
        return budget;
    }
    
    @JsonProperty("budget")
    public void setBudget(Object budget) {
        if (budget instanceof Integer) {
            this.budget = new BigDecimal((Integer) budget);
        } else if (budget instanceof Double) {
            this.budget = BigDecimal.valueOf((Double) budget);
        } else if (budget instanceof String) {
            this.budget = new BigDecimal((String) budget);
        } else if (budget instanceof BigDecimal) {
            this.budget = (BigDecimal) budget;
        }
    }
    
    public LocalDateTime getDeadline() {
        return deadline;
    }
    
    @JsonProperty("deadline")
    public void setDeadline(Object deadline) {
        if (deadline instanceof String) {
            String str = (String) deadline;
            try {
                this.deadline = java.time.OffsetDateTime.parse(str).toLocalDateTime();
            } catch (Exception e) {
                try {
                    this.deadline = java.time.LocalDateTime.parse(str);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Invalid deadline format: " + str);
                }
            }
        } else if (deadline instanceof LocalDateTime) {
            this.deadline = (LocalDateTime) deadline;
        } else {
            throw new IllegalArgumentException("Deadline must be a valid ISO 8601 string or LocalDateTime");
        }
    }
    
    public Project.ProjectType getType() {
        return Project.ProjectType.valueOf(type);
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getAttachmentUrl() {
        return attachmentUrl;
    }
    
    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
} 