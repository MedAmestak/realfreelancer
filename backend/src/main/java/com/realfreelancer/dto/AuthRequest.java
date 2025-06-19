package com.realfreelancer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class AuthRequest {
    @NotBlank(message = "Username is required", groups = Registration.class)
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters", groups = Registration.class)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    private String githubLink;
    private Set<String> skills;
    private String bio;
    
    public interface Registration {}
    public interface Login {}
    
    // Getters and setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getGithubLink() {
        return githubLink;
    }
    
    public void setGithubLink(String githubLink) {
        this.githubLink = githubLink;
    }
    
    public Set<String> getSkills() {
        return skills;
    }
    
    public void setSkills(Set<String> skills) {
        this.skills = skills;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
} 