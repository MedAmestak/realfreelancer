package com.realfreelancer.repository;

import com.realfreelancer.model.Project;
import com.realfreelancer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    Page<Project> findByStatus(Project.ProjectStatus status, Pageable pageable);
    
    Page<Project> findByType(Project.ProjectType type, Pageable pageable);
    
    Page<Project> findByClient(User client, Pageable pageable);
    
    Page<Project> findByFreelancer(User freelancer, Pageable pageable);
    
    List<Project> findByStatusAndDeadlineBefore(Project.ProjectStatus status, LocalDateTime deadline);
    
    // Search methods
    Page<Project> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
        String title, String description, Pageable pageable);
    
    // Budget filtering methods
    Page<Project> findByBudgetBetween(BigDecimal minBudget, BigDecimal maxBudget, Pageable pageable);
    
    Page<Project> findByBudgetGreaterThanEqual(BigDecimal minBudget, Pageable pageable);
    
    Page<Project> findByBudgetLessThanEqual(BigDecimal maxBudget, Pageable pageable);
    
    @Query("SELECT p FROM Project p JOIN p.requiredSkills s WHERE s IN :skills AND p.status = 'OPEN'")
    Page<Project> findByRequiredSkillsAndStatusOpen(@Param("skills") List<String> skills, Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.status = 'OPEN' AND p.type = 'FREE' ORDER BY p.createdAt DESC")
    Page<Project> findOpenFreeProjects(Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.status = 'OPEN' AND p.type = 'PAID' ORDER BY p.budget DESC")
    Page<Project> findOpenPaidProjects(Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.isFeatured = true AND p.status = 'OPEN' ORDER BY p.createdAt DESC")
    List<Project> findFeaturedOpenProjects();
    
    @Query("SELECT p FROM Project p WHERE p.title LIKE %:searchTerm% OR p.description LIKE %:searchTerm%")
    Page<Project> searchProjects(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = 'COMPLETED' AND p.client = :user")
    Long countCompletedProjectsByClient(@Param("user") User user);
    
    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = 'COMPLETED' AND p.freelancer = :user")
    Long countCompletedProjectsByFreelancer(@Param("user") User user);
    
    @Query("SELECT p FROM Project p WHERE p.status = 'OPEN' ORDER BY p.viewCount DESC")
    Page<Project> findMostViewedOpenProjects(Pageable pageable);
    
    @Query("SELECT p FROM Project p WHERE p.status = 'OPEN' ORDER BY p.applicationCount DESC")
    Page<Project> findMostAppliedOpenProjects(Pageable pageable);
} 