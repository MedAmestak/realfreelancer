package com.realfreelancer.dto;

import java.time.LocalDateTime;

public class ConversationSummary {
    private Long userId;
    private String username;
    private String avatarUrl;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;

    public ConversationSummary(Long userId, String username, String avatarUrl, LocalDateTime lastMessageTime, Long unreadCount) {
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getAvatarUrl() { return avatarUrl; }
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public Long getUnreadCount() { return unreadCount; }
} 