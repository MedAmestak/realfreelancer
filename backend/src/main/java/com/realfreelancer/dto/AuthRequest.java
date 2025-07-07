package com.realfreelancer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

import java.util.Set;
import java.util.HashSet;

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

    public interface Registration extends Default {}
    public interface Login extends Default {}
    
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
    
    /**
     * Returns a defensive copy of the skills set.
     */
    public Set<String> getSkills() {
        return (skills == null) ? null : new HashSet<>(skills);
    }
    
    /**
     * Sets skills with a defensive copy and makes it unmodifiable internally.
     */
    public void setSkills(Set<String> skills) {
        this.skills = (skills == null) ? null : java.util.Collections.unmodifiableSet(new HashSet<>(skills));
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
}