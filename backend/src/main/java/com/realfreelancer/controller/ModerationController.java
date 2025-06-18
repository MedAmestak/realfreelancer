package com.realfreelancer.controller;

import com.realfreelancer.model.Report;
import com.realfreelancer.model.User;
import com.realfreelancer.model.Project;
import com.realfreelancer.repository.ReportRepository;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.repository.ProjectRepository;
import com.realfreelancer.service.ModerationService;
import com.realfreelancer.dto.ReportRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/moderation")
@CrossOrigin(origins = "http://localhost:3000")
public class ModerationController {

    @Autowired
    private ModerationService moderationService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Submit a report
    @PostMapping("/report")
    public ResponseEntity<?> submitReport(@Valid @RequestBody ReportRequest reportRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User reporter = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Report report = new Report();
            report.setReporter(reporter);
            report.setReportType(reportRequest.getReportType());
            report.setReason(reportRequest.getReason());
            report.setDescription(reportRequest.getDescription());
            report.setReportedUserId(reportRequest.getReportedUserId());
            report.setReportedProjectId(reportRequest.getReportedProjectId());
            report.setEvidence(reportRequest.getEvidence());

            Report savedReport = moderationService.createReport(report);
            return ResponseEntity.ok(savedReport);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error submitting report: " + e.getMessage());
        }
    }

    // Get all reports (admin only)
    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Report> reports = moderationService.getReports(status, type, pageable);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching reports: " + e.getMessage());
        }
    }

    // Get report by ID
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<?> getReportById(@PathVariable Long reportId) {
        try {
            Optional<Report> report = reportRepository.findById(reportId);
            if (report.isPresent()) {
                return ResponseEntity.ok(report.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching report: " + e.getMessage());
        }
    }

    // Update report status (admin only)
    @PutMapping("/reports/{reportId}/status")
    public ResponseEntity<?> updateReportStatus(
            @PathVariable Long reportId,
            @RequestParam String status,
            @RequestParam(required = false) String adminNotes
    ) {
        try {
            Report updatedReport = moderationService.updateReportStatus(reportId, status, adminNotes);
            return ResponseEntity.ok(updatedReport);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating report status: " + e.getMessage());
        }
    }

    // Take action on reported content (admin only)
    @PostMapping("/action")
    public ResponseEntity<?> takeModerationAction(@RequestBody Map<String, Object> actionRequest) {
        try {
            String actionType = (String) actionRequest.get("actionType");
            Long targetId = Long.valueOf(actionRequest.get("targetId").toString());
            String reason = (String) actionRequest.get("reason");
            String duration = (String) actionRequest.get("duration"); // For suspensions

            Map<String, Object> result = moderationService.takeAction(actionType, targetId, reason, duration);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error taking moderation action: " + e.getMessage());
        }
    }

    // Get moderation statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getModerationStats() {
        try {
            Map<String, Object> stats = moderationService.getModerationStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching moderation stats: " + e.getMessage());
        }
    }

    // Get user's reports
    @GetMapping("/my-reports")
    public ResponseEntity<?> getMyReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Report> reports = reportRepository.findByReporter(user, pageable);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching your reports: " + e.getMessage());
        }
    }

    // Get report types
    @GetMapping("/report-types")
    public ResponseEntity<?> getReportTypes() {
        try {
            List<Map<String, Object>> types = moderationService.getReportTypes();
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching report types: " + e.getMessage());
        }
    }

    // Check if user can report (rate limiting)
    @GetMapping("/can-report")
    public ResponseEntity<?> canReport(@RequestParam Long targetId, @RequestParam String targetType) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            boolean canReport = moderationService.canUserReport(user, targetId, targetType);
            return ResponseEntity.ok(Map.of("canReport", canReport));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking report eligibility: " + e.getMessage());
        }
    }

    // Appeal a moderation action
    @PostMapping("/appeal")
    public ResponseEntity<?> submitAppeal(@RequestBody Map<String, Object> appealRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Long reportId = Long.valueOf(appealRequest.get("reportId").toString());
            String appealReason = (String) appealRequest.get("appealReason");

            Report appeal = moderationService.submitAppeal(user, reportId, appealReason);
            return ResponseEntity.ok(appeal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error submitting appeal: " + e.getMessage());
        }
    }
} 