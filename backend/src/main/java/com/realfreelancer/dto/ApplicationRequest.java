package com.realfreelancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ApplicationRequest {
    
    @NotBlank(message = "Pitch is required")
    @Size(min = 50, max = 1000, message = "Pitch must be between 50 and 1000 characters")
    private String pitch;
    
    @NotNull(message = "Proposed budget is required")
    @Positive(message = "Proposed budget must be positive")
    private BigDecimal proposedBudget;
    
    @NotNull(message = "Estimated duration is required")
    @Positive(message = "Estimated duration must be positive")
    private Integer estimatedDurationDays;
    
    private String attachmentUrl;
    
    // Constructors
    public ApplicationRequest() {}

    public ApplicationRequest(String pitch, BigDecimal proposedBudget, Integer estimatedDurationDays) {
        this.pitch = pitch;
        this.proposedBudget = proposedBudget;
        this.estimatedDurationDays = estimatedDurationDays;
    }
    
    // Getters and Setters
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
    
    public String getAttachmentUrl() {
        return attachmentUrl;
    }
    
    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }
} 