package com.realfreelancer.repository;

import com.realfreelancer.model.Application;
import com.realfreelancer.model.Project;
import com.realfreelancer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    Page<Application> findByProject(Project project, Pageable pageable);
    
    Page<Application> findByFreelancer(User freelancer, Pageable pageable);
    
    List<Application> findByProjectAndStatus(Project project, Application.ApplicationStatus status);
    
    Optional<Application> findByProjectAndFreelancer(Project project, User freelancer);
    
    boolean existsByProjectAndFreelancer(Project project, User freelancer);
    
    @Query("SELECT a FROM Application a WHERE a.project = :project AND a.status = 'PENDING' ORDER BY a.createdAt ASC")
    List<Application> findPendingApplicationsByProject(@Param("project") Project project);
    
    @Query("SELECT a FROM Application a WHERE a.freelancer = :freelancer AND a.status = 'ACCEPTED'")
    List<Application> findAcceptedApplicationsByFreelancer(@Param("freelancer") User freelancer);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.project = :project")
    Long countApplicationsByProject(@Param("project") Project project);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.freelancer = :freelancer AND a.status = 'PENDING'")
    Long countPendingApplicationsByFreelancer(@Param("freelancer") User freelancer);
    
    @Query("SELECT a FROM Application a WHERE a.project.client = :client ORDER BY a.createdAt DESC")
    Page<Application> findApplicationsForClientProjects(@Param("client") User client, Pageable pageable);
} 