package com.realfreelancer.service;

import com.realfreelancer.model.Project;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.ProjectRepository;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    public Page<Project> advancedSearch(
            String query,
            List<String> skills,
            String location,
            BigDecimal minBudget,
            BigDecimal maxBudget,
            String experienceLevel,
            String projectType,
            Pageable pageable
    ) {
        // This is a simplified implementation
        // In a real application, you would use Elasticsearch or similar
        
        if (query != null && !query.trim().isEmpty()) {
            return projectRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                query.trim(), query.trim(), pageable
            );
        }
        
        if (skills != null && !skills.isEmpty()) {
            return projectRepository.findByRequiredSkillsAndStatusOpen(skills, pageable);
        }
        
        if (minBudget != null || maxBudget != null) {
            if (minBudget != null && maxBudget != null) {
                return projectRepository.findByBudgetBetween(minBudget, maxBudget, pageable);
            } else if (minBudget != null) {
                return projectRepository.findByBudgetGreaterThanEqual(minBudget, pageable);
            } else {
                return projectRepository.findByBudgetLessThanEqual(maxBudget, pageable);
            }
        }
        
        // Default: return all open projects
        return projectRepository.findByStatus(Project.ProjectStatus.OPEN, pageable);
    }

    public Page<Project> fullTextSearch(String query, Pageable pageable) {
        return projectRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            query, query, pageable
        );
    }

    public Page<User> searchFreelancers(
            String query,
            List<String> skills,
            String location,
            Double minRating,
            Integer minProjects,
            Pageable pageable
    ) {
        // Simplified implementation
        if (query != null && !query.trim().isEmpty()) {
            return userRepository.findByUsernameContainingIgnoreCaseOrBioContainingIgnoreCase(
                query.trim(), query.trim(), pageable
            );
        }
        
        // Return top-rated freelancers
        return userRepository.findAll(pageable);
    }

    public List<String> getSearchSuggestions(String query) {
        List<String> suggestions = new ArrayList<>();
        
        if (query == null || query.trim().isEmpty()) {
            return suggestions;
        }
        
        String searchTerm = query.trim().toLowerCase();
        
        // Add common project titles
        suggestions.add(searchTerm + " development");
        suggestions.add(searchTerm + " design");
        suggestions.add(searchTerm + " app");
        suggestions.add(searchTerm + " website");
        suggestions.add(searchTerm + " system");
        
        // Add common skills
        suggestions.add("Java " + searchTerm);
        suggestions.add("React " + searchTerm);
        suggestions.add("Python " + searchTerm);
        suggestions.add("Node.js " + searchTerm);
        
        return suggestions.stream().limit(10).collect(Collectors.toList());
    }

    public Page<Project> getProjectRecommendations(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get user's skills
        Set<String> userSkills = user.getSkills();
        
        if (userSkills.isEmpty()) {
            // If no skills, return featured projects
            return projectRepository.findByStatus(Project.ProjectStatus.OPEN, pageable);
        }
        
        // Find projects that match user's skills
        return projectRepository.findByRequiredSkillsAndStatusOpen(
            new ArrayList<>(userSkills), pageable
        );
    }
} 