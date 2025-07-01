package com.realfreelancer.service;

import com.realfreelancer.repository.ProjectRepository;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.repository.ApplicationRepository;
import com.realfreelancer.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class AnalyticsService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Map<String, Object>> getTrendingSkills(int limit, int days) {
        List<Map<String, Object>> trendingSkills = new ArrayList<>();
        
        // Mock data for trending skills
        String[] skills = {"Java", "React", "Python", "Node.js", "Spring Boot", "TypeScript", "Docker", "AWS", "MongoDB", "PostgreSQL"};
        int[] counts = {156, 142, 128, 115, 98, 87, 76, 65, 54, 43};
        
        for (int i = 0; i < Math.min(limit, skills.length); i++) {
            Map<String, Object> skill = new HashMap<>();
            skill.put("skill", skills[i]);
            skill.put("count", counts[i]);
            skill.put("growth", Math.random() * 20 + 5); // Random growth percentage
            trendingSkills.add(skill);
        }
        
        return trendingSkills;
    }

    public Map<String, Object> getSearchAnalytics(int days) {
        Map<String, Object> analytics = new HashMap<>();
        
        // Mock analytics data
        analytics.put("totalSearches", 15420);
        analytics.put("uniqueUsers", 3240);
        analytics.put("averageSearchTime", 2.3);
        analytics.put("popularQueries", List.of("web development", "mobile app", "UI/UX design", "backend", "frontend"));
        analytics.put("searchSuccessRate", 87.5);
        
        return analytics;
    }

    public List<Map<String, Object>> getPopularSearches(int limit) {
        List<Map<String, Object>> popularSearches = new ArrayList<>();
        
        String[] queries = {"web development", "mobile app", "UI/UX design", "backend development", "frontend development"};
        int[] counts = {1250, 980, 876, 654, 543};
        
        for (int i = 0; i < Math.min(limit, queries.length); i++) {
            Map<String, Object> search = new HashMap<>();
            search.put("query", queries[i]);
            search.put("count", counts[i]);
            search.put("trend", Math.random() > 0.5 ? "up" : "down");
            popularSearches.add(search);
        }
        
        return popularSearches;
    }

    public Map<String, Object> getPlatformAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        // Get real data from repositories
        long totalProjects = projectRepository.count();
        long totalUsers = userRepository.count();
        long totalApplications = applicationRepository.count();
        long totalReviews = reviewRepository.count();
        
        analytics.put("totalProjects", totalProjects);
        analytics.put("totalUsers", totalUsers);
        analytics.put("totalApplications", totalApplications);
        analytics.put("totalReviews", totalReviews);
        analytics.put("averageProjectBudget", 2500.0);
        analytics.put("averageUserRating", 4.2);
        analytics.put("projectCompletionRate", 78.5);
        
        return analytics;
    }

    public Map<String, Object> getUserAnalytics(String username) {
        Map<String, Object> analytics = new HashMap<>();
        
        // Mock user analytics
        analytics.put("projectsPosted", 12);
        analytics.put("projectsCompleted", 8);
        analytics.put("applicationsSubmitted", 45);
        analytics.put("applicationsAccepted", 12);
        analytics.put("averageRating", 4.7);
        analytics.put("totalEarnings", 8500.0);
        analytics.put("responseTime", 2.1);
        analytics.put("completionRate", 85.0);
        
        return analytics;
    }
}