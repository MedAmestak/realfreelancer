package com.realfreelancer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private BigDecimal budget;
    private LocalDateTime deadline;
    private Set<String> requiredSkills;
    private String status;
    private String type;
    private ClientDTO client;

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
    public Set<String> getRequiredSkills() { 
        return requiredSkills != null ? new HashSet<>(requiredSkills) : new HashSet<>(); 
    }
    public void setRequiredSkills(Set<String> requiredSkills) { 
        this.requiredSkills = requiredSkills != null ? new HashSet<>(requiredSkills) : new HashSet<>(); 
    }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public ClientDTO getClient() { return client; }
    public void setClient(ClientDTO client) { this.client = client; }
} 