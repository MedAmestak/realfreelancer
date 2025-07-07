package com.realfreelancer.controller;

import com.realfreelancer.service.AnalyticsService;
import com.realfreelancer.service.ProjectService;
import com.realfreelancer.service.ReviewService;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.repository.ProjectRepository;
import com.realfreelancer.repository.ApplicationRepository;
import com.realfreelancer.repository.ReviewRepository;
import com.realfreelancer.dto.ApplicationDTO;
import com.realfreelancer.dto.ProjectDTO;
import com.realfreelancer.dto.ClientDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // Get user dashboard data
    @GetMapping("/user")
    public ResponseEntity<?> getUserDashboard() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> dashboard = new HashMap<>();
            
            // User analytics
            Map<String, Object> userAnalytics = analyticsService.getUserAnalytics(user.getUsername());
            dashboard.put("analytics", userAnalytics);
            
            // Recent projects (map to DTO)
            var recentProjects = projectRepository.findByClient(user,
                org.springframework.data.domain.PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("createdAt").descending())).getContent();
            dashboard.put("recentProjects", recentProjects.stream().map(this::toProjectDTO).toList());
            
            // Recent applications (map to DTO)
            var recentApplications = applicationRepository.findByFreelancer(user,
                org.springframework.data.domain.PageRequest.of(0, 5, org.springframework.data.domain.Sort.by("createdAt").descending())).getContent();
            dashboard.put("recentApplications", recentApplications.stream().map(this::toApplicationDTO).toList());
            
            // Quick stats
            dashboard.put("quickStats", Map.of(
                "unreadMessages", 3,
                "pendingApplications", 8,
                "activeProjects", 2,
                "completedProjects", 15
            ));
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching dashboard: " + e.getMessage());
        }
    }

    // Get platform overview (admin)
    @GetMapping("/platform")
    public ResponseEntity<?> getPlatformOverview() {
        try {
            Map<String, Object> overview = new HashMap<>();
            
            // Platform analytics
            Map<String, Object> platformAnalytics = analyticsService.getPlatformAnalytics();
            overview.put("analytics", platformAnalytics);
            
            // Recent activity
            overview.put("recentProjects", projectRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 10, 
                    org.springframework.data.domain.Sort.by("createdAt").descending())).getContent());
            
            // Trending skills
            overview.put("trendingSkills", analyticsService.getTrendingSkills(5, 7));
            
            // Popular searches
            overview.put("popularSearches", analyticsService.getPopularSearches(5));
            
            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching platform overview: " + e.getMessage());
        }
    }

    // Get user statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> stats = new HashMap<>();
            
            // Simplified statistics using available methods
            long totalProjects = projectRepository.count();
            long totalApplications = applicationRepository.count();
            long totalReviews = reviewRepository.count();
            
            stats.put("totalProjects", totalProjects);
            stats.put("totalApplications", totalApplications);
            stats.put("totalReviews", totalReviews);
            stats.put("averageRating", 4.5); // Mock data
            stats.put("completionRate", 85.0); // Mock data
            stats.put("acceptanceRate", 75.0); // Mock data
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching user stats: " + e.getMessage());
        }
    }

    // Get earnings overview
    @GetMapping("/earnings")
    public ResponseEntity<?> getEarningsOverview(
            @RequestParam(defaultValue = "30") int days
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> earnings = new HashMap<>();
            
            // Mock earnings data
            earnings.put("totalEarnings", 12500.0);
            earnings.put("monthlyEarnings", 3200.0);
            earnings.put("weeklyEarnings", 850.0);
            earnings.put("averagePerProject", 1250.0);
            earnings.put("projectsThisMonth", 8);
            earnings.put("projectsThisWeek", 2);
            
            // Earnings trend (last 30 days)
            List<Map<String, Object>> trend = new ArrayList<>();
            for (int i = 29; i >= 0; i--) {
                Map<String, Object> day = new HashMap<>();
                day.put("date", LocalDateTime.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                day.put("earnings", Math.random() * 200 + 50); // Random daily earnings
                trend.add(day);
            }
            earnings.put("trend", trend);
            
            return ResponseEntity.ok(earnings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching earnings: " + e.getMessage());
        }
    }

    // Get performance metrics
    @GetMapping("/performance")
    public ResponseEntity<?> getPerformanceMetrics() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> performance = new HashMap<>();
            
            // Mock performance data
            performance.put("responseTime", 2.1); // hours
            performance.put("completionRate", 85.0); // percentage
            performance.put("clientSatisfaction", 4.7); // rating
            performance.put("onTimeDelivery", 92.0); // percentage
            performance.put("communicationScore", 4.8); // rating
            performance.put("qualityScore", 4.6); // rating
            
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching performance metrics: " + e.getMessage());
        }
    }

    // --- DTO mapping helpers ---
    private ProjectDTO toProjectDTO(com.realfreelancer.model.Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setBudget(project.getBudget());
        dto.setDeadline(project.getDeadline());
        dto.setRequiredSkills(project.getRequiredSkills());
        dto.setStatus(project.getStatus().name());
        dto.setType(project.getType().name());
        dto.setCreatedAt(project.getCreatedAt());
        if (project.getClient() != null) {
            dto.setClient(new ClientDTO(
                project.getClient().getId(),
                project.getClient().getUsername(),
                project.getClient().getEmail(),
                Boolean.TRUE.equals(project.getClient().getIsVerified())
            ));
        }
        return dto;
    }

    private ApplicationDTO toApplicationDTO(com.realfreelancer.model.Application app) {
        return new ApplicationDTO(
            app.getId(),
            app.getPitch(),
            app.getProposedBudget(),
            app.getEstimatedDurationDays(),
            app.getStatus() != null ? app.getStatus().name() : null,
            app.getAttachmentUrl(),
            app.getCreatedAt(),
            app.getUpdatedAt(),
            app.getProject() != null ? app.getProject().getId() : null,
            app.getProject() != null ? app.getProject().getTitle() : null,
            app.getFreelancer() != null ? app.getFreelancer().getId() : null,
            app.getFreelancer() != null ? app.getFreelancer().getUsername() : null
        );
    }
} 