package com.realfreelancer.service;

import com.realfreelancer.model.Report;
import com.realfreelancer.model.User;
import com.realfreelancer.model.Project;
import com.realfreelancer.repository.ReportRepository;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ModerationService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public Report createReport(Report report) {
        // Check rate limiting
        if (!canUserReport(report.getReporter(), 
            report.getReportedUserId() != null ? report.getReportedUserId() : report.getReportedProjectId(),
            report.getReportedUserId() != null ? "USER" : "PROJECT")) {
            throw new RuntimeException("Rate limit exceeded. Please wait before submitting another report.");
        }
        
        return reportRepository.save(report);
    }

    public Page<Report> getReports(String status, String type, Pageable pageable) {
        if (status != null && !status.trim().isEmpty()) {
            try {
                Report.ReportStatus reportStatus = Report.ReportStatus.valueOf(status.toUpperCase());
                return reportRepository.findByStatus(reportStatus, pageable);
            } catch (IllegalArgumentException e) {
                return reportRepository.findAll(pageable);
            }
        }
        
        if (type != null && !type.trim().isEmpty()) {
            try {
                Report.ReportType reportType = Report.ReportType.valueOf(type.toUpperCase());
                return reportRepository.findByReportType(reportType, pageable);
            } catch (IllegalArgumentException e) {
                return reportRepository.findAll(pageable);
            }
        }
        
        return reportRepository.findAll(pageable);
    }

    public Report updateReportStatus(Long reportId, String status, String adminNotes) {
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (!reportOpt.isPresent()) {
            throw new RuntimeException("Report not found");
        }

        Report report = reportOpt.get();
        try {
            Report.ReportStatus reportStatus = Report.ReportStatus.valueOf(status.toUpperCase());
            report.setStatus(reportStatus);
            report.setAdminNotes(adminNotes);
            report.setResolvedBy("admin");
            
            return reportRepository.save(report);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }

    public Map<String, Object> takeAction(String actionType, Long targetId, String reason, String duration) {
        Map<String, Object> result = new HashMap<>();
        
        switch (actionType.toUpperCase()) {
            case "SUSPEND_USER":
                result = suspendUser(targetId, reason, duration);
                break;
            case "DELETE_PROJECT":
                result = deleteProject(targetId, reason);
                break;
            case "WARN_USER":
                result = warnUser(targetId, reason);
                break;
            case "HIDE_PROJECT":
                result = hideProject(targetId, reason);
                break;
            default:
                throw new RuntimeException("Invalid action type: " + actionType);
        }
        
        return result;
    }

    public Map<String, Object> getModerationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalReports = reportRepository.count();
        long pendingReports = reportRepository.countPendingReports();
        long resolvedReports = reportRepository.countByStatus(Report.ReportStatus.RESOLVED);
        long dismissedReports = reportRepository.countByStatus(Report.ReportStatus.DISMISSED);
        
        stats.put("totalReports", totalReports);
        stats.put("pendingReports", pendingReports);
        stats.put("resolvedReports", resolvedReports);
        stats.put("dismissedReports", dismissedReports);
        stats.put("resolutionRate", totalReports > 0 ? (double) (resolvedReports + dismissedReports) / totalReports * 100 : 0.0);
        
        return stats;
    }

    public List<Map<String, Object>> getReportTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        
        for (Report.ReportType type : Report.ReportType.values()) {
            Map<String, Object> typeInfo = new HashMap<>();
            typeInfo.put("type", type.name());
            typeInfo.put("displayName", getDisplayNameForType(type));
            typeInfo.put("description", getDescriptionForType(type));
            types.add(typeInfo);
        }
        
        return types;
    }

    public boolean canUserReport(User user, Long targetId, String targetType) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        Long recentReports = reportRepository.countRecentReportsByUser(user, since);
        
        return recentReports < 5;
    }

    public Report submitAppeal(User user, Long reportId, String appealReason) {
        Optional<Report> reportOpt = reportRepository.findById(reportId);
        if (!reportOpt.isPresent()) {
            throw new RuntimeException("Report not found");
        }

        Report report = reportOpt.get();
        if (!report.getReporter().getId().equals(user.getId())) {
            throw new RuntimeException("You can only appeal your own reports");
        }

        report.setAppealReason(appealReason);
        report.setAppealStatus("PENDING");
        report.setStatus(Report.ReportStatus.APPEALED);
        
        return reportRepository.save(report);
    }

    private Map<String, Object> suspendUser(Long userId, String reason, String duration) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        
        Map<String, Object> result = new HashMap<>();
        result.put("action", "USER_SUSPENDED");
        result.put("userId", userId);
        result.put("username", user.getUsername());
        result.put("reason", reason);
        result.put("duration", duration);
        result.put("suspendedAt", LocalDateTime.now());
        
        return result;
    }

    private Map<String, Object> deleteProject(Long projectId, String reason) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (!projectOpt.isPresent()) {
            throw new RuntimeException("Project not found");
        }

        Project project = projectOpt.get();
        projectRepository.delete(project);
        
        Map<String, Object> result = new HashMap<>();
        result.put("action", "PROJECT_DELETED");
        result.put("projectId", projectId);
        result.put("projectTitle", project.getTitle());
        result.put("reason", reason);
        result.put("deletedAt", LocalDateTime.now());
        
        return result;
    }

    private Map<String, Object> warnUser(Long userId, String reason) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        
        Map<String, Object> result = new HashMap<>();
        result.put("action", "USER_WARNED");
        result.put("userId", userId);
        result.put("username", user.getUsername());
        result.put("reason", reason);
        result.put("warnedAt", LocalDateTime.now());
        
        return result;
    }

    private Map<String, Object> hideProject(Long projectId, String reason) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (!projectOpt.isPresent()) {
            throw new RuntimeException("Project not found");
        }

        Project project = projectOpt.get();
        
        Map<String, Object> result = new HashMap<>();
        result.put("action", "PROJECT_HIDDEN");
        result.put("projectId", projectId);
        result.put("projectTitle", project.getTitle());
        result.put("reason", reason);
        result.put("hiddenAt", LocalDateTime.now());
        
        return result;
    }

    private String getDisplayNameForType(Report.ReportType type) {
        return switch (type) {
            case SPAM -> "Spam";
            case INAPPROPRIATE_CONTENT -> "Inappropriate Content";
            case HARASSMENT -> "Harassment";
            case FRAUD -> "Fraud";
            case COPYRIGHT_VIOLATION -> "Copyright Violation";
            case MISLEADING_INFORMATION -> "Misleading Information";
            case OFFENSIVE_LANGUAGE -> "Offensive Language";
            case OTHER -> "Other";
        };
    }

    private String getDescriptionForType(Report.ReportType type) {
        return switch (type) {
            case SPAM -> "Unwanted or repetitive content";
            case INAPPROPRIATE_CONTENT -> "Content that violates community guidelines";
            case HARASSMENT -> "Bullying, threats, or abusive behavior";
            case FRAUD -> "Scams, fake projects, or payment fraud";
            case COPYRIGHT_VIOLATION -> "Unauthorized use of copyrighted material";
            case MISLEADING_INFORMATION -> "False or deceptive information";
            case OFFENSIVE_LANGUAGE -> "Hate speech or offensive language";
            case OTHER -> "Other violations not covered above";
        };
    }
} 