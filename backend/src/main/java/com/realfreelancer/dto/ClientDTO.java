package com.realfreelancer.dto;

public class ClientDTO {
    private Long id;
    private String username;
    private String email;
    private boolean isVerified;

    public ClientDTO(Long id, String username, String email, boolean isVerified) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.isVerified = isVerified;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }
} 