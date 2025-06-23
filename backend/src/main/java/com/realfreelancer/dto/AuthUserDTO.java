package com.realfreelancer.dto;

import com.realfreelancer.model.User;
import java.util.Set;

public class AuthUserDTO {
    private Long id;
    private String username;
    private String email;
    private String githubLink;
    private String bio;
    private String avatarUrl;
    private Integer reputationPoints;
    private Boolean isVerified;
    private Set<String> skills;

    public AuthUserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.githubLink = user.getGithubLink();
        this.bio = user.getBio();
        this.avatarUrl = user.getAvatarUrl();
        this.reputationPoints = user.getReputationPoints();
        this.isVerified = user.getIsVerified();
        this.skills = user.getSkills();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGithubLink() { return githubLink; }
    public void setGithubLink(String githubLink) { this.githubLink = githubLink; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public Integer getReputationPoints() { return reputationPoints; }
    public void setReputationPoints(Integer reputationPoints) { this.reputationPoints = reputationPoints; }
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public Set<String> getSkills() { return skills; }
    public void setSkills(Set<String> skills) { this.skills = skills; }
} 