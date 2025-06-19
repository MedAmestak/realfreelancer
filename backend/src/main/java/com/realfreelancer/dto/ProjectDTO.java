package com.realfreelancer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal budget;
    private LocalDateTime deadline;
    private Set<String> requiredSkills;
    private String status;
    private String type;
    private String clientUsername;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public Set<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(Set<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getClientUsername() { return clientUsername; }
    public void setClientUsername(String clientUsername) { this.clientUsername = clientUsername; }
} 