package com.realfreelancer.dto;

import com.realfreelancer.model.User;
import java.util.Set;

public class AuthUserDTO {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String githubLink;
    private Set<String> skills;
    private Integer reputationPoints;
    private boolean isVerified;
    private java.util.Map<String, Object> stats;

    public AuthUserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.githubLink = user.getGithubLink();
        this.skills = user.getSkills();
        this.reputationPoints = user.getReputationPoints();
        this.isVerified = user.getIsVerified();
    }

    public AuthUserDTO(User user, java.util.Map<String, Object> stats) {
        this(user);
        this.stats = stats;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }
    public Set<String> getSkills() { return (skills == null) ? null : new java.util.HashSet<>(skills); }
    public void setSkills(Set<String> skills) { this.skills = (skills == null) ? null : new java.util.HashSet<>(skills); }
    public Integer getReputationPoints() { return reputationPoints; }
    public void setReputationPoints(Integer reputationPoints) { this.reputationPoints = reputationPoints; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean isVerified) { this.isVerified = isVerified; }
    public java.util.Map<String, Object> getStats() { return stats; }
    public void setStats(java.util.Map<String, Object> stats) { this.stats = stats; }
} 