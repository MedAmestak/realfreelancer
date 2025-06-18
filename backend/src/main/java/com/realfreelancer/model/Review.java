package com.realfreelancer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@EntityListeners(AuditingEntityListener.class)
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(nullable = false)
    private Integer rating;
    
    @NotBlank(message = "Comment is required")
    @Size(min = 10, max = 500, message = "Comment must be between 10 and 500 characters")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_user_id", nullable = false)
    private User reviewedUser;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewType type = ReviewType.CLIENT_TO_FREELANCER;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Enum
    public enum ReviewType {
        CLIENT_TO_FREELANCER, FREELANCER_TO_CLIENT
    }
    
    // Constructors
    public Review() {}
    
    public Review(Integer rating, String comment, Project project, User reviewer, User reviewedUser) {
        this.rating = rating;
        this.comment = comment;
        this.project = project;
        this.reviewer = reviewer;
        this.reviewedUser = reviewedUser;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Project getProject() {
        return project;
    }
    
    public void setProject(Project project) {
        this.project = project;
    }
    
    public User getReviewer() {
        return reviewer;
    }
    
    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }
    
    public User getReviewedUser() {
        return reviewedUser;
    }
    
    public void setReviewedUser(User reviewedUser) {
        this.reviewedUser = reviewedUser;
    }
    
    public ReviewType getType() {
        return type;
    }
    
    public void setType(ReviewType type) {
        this.type = type;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Helper methods
    public boolean isFiveStarReview() {
        return this.rating == 5;
    }
    
    public boolean isHighRating() {
        return this.rating >= 4;
    }
    
    public boolean isLowRating() {
        return this.rating <= 2;
    }
    
    public int getReputationPoints() {
        // Award points based on rating
        switch (this.rating) {
            case 5: return 10;
            case 4: return 5;
            case 3: return 2;
            case 2: return 0;
            case 1: return -5;
            default: return 0;
        }
    }
} 