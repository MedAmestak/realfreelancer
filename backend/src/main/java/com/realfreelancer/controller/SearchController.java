package com.realfreelancer.controller;

import com.realfreelancer.model.Project;
import com.realfreelancer.model.User;
import com.realfreelancer.service.SearchService;
import com.realfreelancer.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "http://localhost:3000")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private AnalyticsService analyticsService;

    // Advanced search with multiple criteria
    @GetMapping("/advanced")
    public ResponseEntity<?> advancedSearch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minBudget,
            @RequestParam(required = false) BigDecimal maxBudget,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String projectType,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.fromString(sortOrder), sortBy != null ? sortBy : "createdAt"));
            
            Page<Project> results = searchService.advancedSearch(
                query, skills, location, minBudget, maxBudget, 
                experienceLevel, projectType, pageable
            );
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error performing search: " + e.getMessage());
        }
    }

    // Full-text search across projects
    @GetMapping("/fulltext")
    public ResponseEntity<?> fullTextSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Project> results = searchService.fullTextSearch(query, pageable);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error performing full-text search: " + e.getMessage());
        }
    }

    // Search for freelancers
    @GetMapping("/freelancers")
    public ResponseEntity<?> searchFreelancers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) List<String> skills,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minProjects
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("reputationPoints").descending());
            Page<User> results = searchService.searchFreelancers(
                query, skills, location, minRating, minProjects, pageable
            );
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error searching freelancers: " + e.getMessage());
        }
    }

    // Get search suggestions
    @GetMapping("/suggestions")
    public ResponseEntity<?> getSearchSuggestions(@RequestParam String query) {
        try {
            List<String> suggestions = searchService.getSearchSuggestions(query);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting suggestions: " + e.getMessage());
        }
    }

    // Get trending skills
    @GetMapping("/trending-skills")
    public ResponseEntity<?> getTrendingSkills(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "7") int days
    ) {
        try {
            List<Map<String, Object>> trendingSkills = analyticsService.getTrendingSkills(limit, days);
            return ResponseEntity.ok(trendingSkills);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting trending skills: " + e.getMessage());
        }
    }

    // Get project recommendations for user
    @GetMapping("/recommendations")
    public ResponseEntity<?> getProjectRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Project> recommendations = searchService.getProjectRecommendations(username, pageable);
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting recommendations: " + e.getMessage());
        }
    }

    // Get search analytics
    @GetMapping("/analytics")
    public ResponseEntity<?> getSearchAnalytics(
            @RequestParam(defaultValue = "30") int days
    ) {
        try {
            Map<String, Object> analytics = analyticsService.getSearchAnalytics(days);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting search analytics: " + e.getMessage());
        }
    }

    // Get popular searches
    @GetMapping("/popular-searches")
    public ResponseEntity<?> getPopularSearches(
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            List<Map<String, Object>> popularSearches = analyticsService.getPopularSearches(limit);
            return ResponseEntity.ok(popularSearches);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error getting popular searches: " + e.getMessage());
        }
    }
} 