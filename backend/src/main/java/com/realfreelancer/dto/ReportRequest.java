package com.realfreelancer.dto;

import com.realfreelancer.model.Report;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReportRequest {
    
    @NotNull(message = "Report type is required")
    private Report.ReportType reportType;
    
    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 200, message = "Reason must be between 10 and 200 characters")
    private String reason;
    
    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 1000, message = "Description must be between 20 and 1000 characters")
    private String description;
    
    private Long reportedUserId;
    
    private Long reportedProjectId;
    
    private String evidence;
    
    // Constructors
    public ReportRequest() {}
    
    public ReportRequest(Report.ReportType reportType, String reason, String description) {
        this.reportType = reportType;
        this.reason = reason;
        this.description = description;
    }
    
    // Getters and Setters
    public Report.ReportType getReportType() {
        return reportType;
    }
    
    public void setReportType(Report.ReportType reportType) {
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
    
    public String getEvidence() {
        return evidence;
    }
    
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }
} 