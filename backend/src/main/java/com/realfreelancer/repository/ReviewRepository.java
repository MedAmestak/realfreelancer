package com.realfreelancer.repository;

import com.realfreelancer.model.Review;
import com.realfreelancer.model.User;
import com.realfreelancer.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Page<Review> findByReviewedUser(User reviewedUser, Pageable pageable);
    
    Page<Review> findByReviewer(User reviewer, Pageable pageable);
    
    List<Review> findByReviewedUserAndRating(User reviewedUser, Integer rating);
    
    List<Review> findByProject(Project project);
    
    boolean existsByProjectAndReviewer(Project project, User reviewer);
    
    Long countByReviewedUser(User reviewedUser);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser = :user")
    Double findAverageRatingByReviewedUser(@Param("user") User user);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewedUser = :user AND r.rating = 5")
    Long countFiveStarReviewsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewedUser = :user")
    Long countTotalReviewsByUser(@Param("user") User user);
    
    @Query("SELECT r FROM Review r WHERE r.reviewedUser = :user AND r.rating >= 4")
    List<Review> findPositiveReviewsByUser(@Param("user") User user);
    
    @Query("SELECT r FROM Review r WHERE r.reviewedUser = :user ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsByUser(@Param("user") User user);
    
    @Query("SELECT r FROM Review r WHERE r.reviewer = :user AND r.reviewedUser = :reviewedUser")
    List<Review> findByReviewerAndReviewedUser(@Param("user") User user, @Param("reviewedUser") User reviewedUser);
    
    @Query("SELECT r FROM Review r WHERE r.reviewedUser = :user AND r.isVerified = true")
    List<Review> findVerifiedReviewsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewedUser = :user AND r.createdAt >= :daysAgo")
    Long countRecentReviewsByUser(@Param("user") User user, @Param("daysAgo") int daysAgo);
    
    @Query("SELECT u FROM User u ORDER BY (SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser = u) DESC")
    List<User> findTopRatedUsers(@Param("limit") int limit);
} 