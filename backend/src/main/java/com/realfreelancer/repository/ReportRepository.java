package com.realfreelancer.repository;

import com.realfreelancer.model.Report;
import com.realfreelancer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    Page<Report> findByReporter(User reporter, Pageable pageable);
    
    Page<Report> findByStatus(Report.ReportStatus status, Pageable pageable);
    
    Page<Report> findByReportType(Report.ReportType reportType, Pageable pageable);
    
    Long countByStatus(Report.ReportStatus status);
    
    @Query("SELECT r FROM Report r WHERE r.reportedUserId = :userId")
    Page<Report> findByReportedUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT r FROM Report r WHERE r.reportedProjectId = :projectId")
    Page<Report> findByReportedProjectId(@Param("projectId") Long projectId, Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reporter = :user AND r.createdAt >= :since")
    Long countRecentReportsByUser(@Param("user") User user, @Param("since") java.time.LocalDateTime since);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportedUserId = :userId AND r.status = 'RESOLVED'")
    Long countResolvedReportsByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = 'PENDING'")
    Long countPendingReports();
} 