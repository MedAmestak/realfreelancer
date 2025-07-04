package com.realfreelancer.dto;

import java.time.LocalDateTime;
import com.realfreelancer.model.Notification;

public class NotificationDTO {
    private Long id;
    private String type;
    private String title;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private String link;
    private String icon;

    public NotificationDTO(Notification n) {
        this.id = n.getId();
        this.type = n.getType().name();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.isRead = n.getIsRead();
        this.createdAt = n.getCreatedAt();
        this.link = n.getActionUrl();
        this.icon = n.getIcon();
}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
} 