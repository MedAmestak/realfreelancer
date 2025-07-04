package com.realfreelancer.dto;

import java.time.LocalDateTime;

public class ConversationSummary {
    private Long conversationId;
    private Long userId;
    private String username;
    private String avatarUrl;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;

    public ConversationSummary(Long conversationId, Long userId, String username, String avatarUrl, LocalDateTime lastMessageTime, Long unreadCount) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
    }

    public Long getConversationId() { return conversationId; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getAvatarUrl() { return avatarUrl; }
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public Long getUnreadCount() { return unreadCount; }
}