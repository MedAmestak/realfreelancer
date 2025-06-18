package com.realfreelancer.service;

import com.realfreelancer.model.Project;
import com.realfreelancer.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public Page<Project> getFilteredProjects(
            String status,
            String type,
            List<String> skills,
            BigDecimal minBudget,
            BigDecimal maxBudget,
            String search,
            Pageable pageable
    ) {
        // This is a simplified implementation
        // In a real application, you would use Specification or Criteria API
        // for more complex filtering
        
        if (search != null && !search.trim().isEmpty()) {
            return projectRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                search.trim(), search.trim(), pageable
            );
        }
        
        if (status != null && !status.trim().isEmpty()) {
            try {
                Project.ProjectStatus projectStatus = Project.ProjectStatus.valueOf(status.toUpperCase());
                return projectRepository.findByStatus(projectStatus, pageable);
            } catch (IllegalArgumentException e) {
                // Invalid status, return all projects
                return projectRepository.findAll(pageable);
            }
        }
        
        if (type != null && !type.trim().isEmpty()) {
            try {
                Project.ProjectType projectType = Project.ProjectType.valueOf(type.toUpperCase());
                return projectRepository.findByType(projectType, pageable);
            } catch (IllegalArgumentException e) {
                // Invalid type, return all projects
                return projectRepository.findAll(pageable);
            }
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
        
        // Default: return all projects
        return projectRepository.findAll(pageable);
    }

    public List<Project> getFeaturedProjects() {
        return projectRepository.findFeaturedOpenProjects();
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
} 