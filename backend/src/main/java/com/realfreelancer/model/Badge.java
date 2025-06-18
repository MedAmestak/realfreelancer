package com.realfreelancer.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
@EntityListeners(AuditingEntityListener.class)
public class Badge {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Badge name is required")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Badge description is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeType type = BadgeType.REPUTATION;
    
    @Column(name = "required_points")
    private Integer requiredPoints = 0;
    
    @Column(name = "required_reviews")
    private Integer requiredReviews = 0;
    
    @Column(name = "required_projects")
    private Integer requiredProjects = 0;
    
    @Column(name = "is_rare")
    private Boolean isRare = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreatedDate
    @Column(name = "earned_at", nullable = false, updatable = false)
    private LocalDateTime earnedAt;
    
    // Enum
    public enum BadgeType {
        REPUTATION,    // Based on points/reviews
        PROJECTS,      // Based on completed projects
        SKILLS,        // Based on skill diversity
        SPECIAL        // Special achievements
    }
    
    // Constructors
    public Badge() {}
    
    public Badge(String name, String description, BadgeType type, User user) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getIconUrl() {
        return iconUrl;
    }
    
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    
    public BadgeType getType() {
        return type;
    }
    
    public void setType(BadgeType type) {
        this.type = type;
    }
    
    public Integer getRequiredPoints() {
        return requiredPoints;
    }
    
    public void setRequiredPoints(Integer requiredPoints) {
        this.requiredPoints = requiredPoints;
    }
    
    public Integer getRequiredReviews() {
        return requiredReviews;
    }
    
    public void setRequiredReviews(Integer requiredReviews) {
        this.requiredReviews = requiredReviews;
    }
    
    public Integer getRequiredProjects() {
        return requiredProjects;
    }
    
    public void setRequiredProjects(Integer requiredProjects) {
        this.requiredProjects = requiredProjects;
    }
    
    public Boolean getIsRare() {
        return isRare;
    }
    
    public void setIsRare(Boolean isRare) {
        this.isRare = isRare;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }
    
    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }
    
    // Helper methods
    public boolean isReputationBadge() {
        return this.type == BadgeType.REPUTATION;
    }
    
    public boolean isProjectBadge() {
        return this.type == BadgeType.PROJECTS;
    }
    
    public boolean isSkillBadge() {
        return this.type == BadgeType.SKILLS;
    }
    
    public boolean isSpecialBadge() {
        return this.type == BadgeType.SPECIAL;
    }
    
    // Static badge definitions
    public static Badge createTaskTitan(User user) {
        Badge badge = new Badge("Task Titan", "Completed 5+ projects with 5-star reviews", BadgeType.REPUTATION, user);
        badge.setRequiredProjects(5);
        badge.setRequiredPoints(50);
        badge.setIconUrl("/badges/task-titan.png");
        return badge;
    }
    
    public static Badge createSkillMaster(User user) {
        Badge badge = new Badge("Skill Master", "Demonstrated expertise in 5+ different skills", BadgeType.SKILLS, user);
        badge.setIconUrl("/badges/skill-master.png");
        return badge;
    }
    
    public static Badge createClientFavorite(User user) {
        Badge badge = new Badge("Client Favorite", "Received 10+ positive reviews from clients", BadgeType.REPUTATION, user);
        badge.setRequiredReviews(10);
        badge.setIconUrl("/badges/client-favorite.png");
        return badge;
    }
    
    public static Badge createRisingStar(User user) {
        Badge badge = new Badge("Rising Star", "First project completed successfully", BadgeType.PROJECTS, user);
        badge.setRequiredProjects(1);
        badge.setIconUrl("/badges/rising-star.png");
        return badge;
    }
} 