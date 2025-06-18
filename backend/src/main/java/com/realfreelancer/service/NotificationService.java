package com.realfreelancer.service;

import com.realfreelancer.model.Notification;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.NotificationRepository;
import com.realfreelancer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Notification createNotification(User user, String title, String message, 
                                        Notification.NotificationType type, String actionUrl) {
        Notification notification = new Notification(user, title, message, type);
        notification.setActionUrl(actionUrl);
        notification.setIcon(getIconForType(type));
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSendToUser(
            user.getUsername(),
            "/queue/notifications",
            savedNotification
        );
        
        return savedNotification;
    }

    public void createProjectApplicationNotification(User client, String projectTitle, String freelancerName) {
        String title = "New Project Application";
        String message = freelancerName + " has applied for your project: " + projectTitle;
        String actionUrl = "/projects/applications";
        
        createNotification(client, title, message, Notification.NotificationType.PROJECT_APPLICATION, actionUrl);
    }

    public void createProjectAcceptedNotification(User freelancer, String projectTitle) {
        String title = "Application Accepted!";
        String message = "Your application for '" + projectTitle + "' has been accepted!";
        String actionUrl = "/projects/active";
        
        createNotification(freelancer, title, message, Notification.NotificationType.PROJECT_ACCEPTED, actionUrl);
    }

    public void createNewMessageNotification(User recipient, String senderName) {
        String title = "New Message";
        String message = "You have a new message from " + senderName;
        String actionUrl = "/chat";
        
        createNotification(recipient, title, message, Notification.NotificationType.NEW_MESSAGE, actionUrl);
    }

    public void createReviewReceivedNotification(User user, String reviewerName, Integer rating) {
        String title = "New Review Received";
        String message = reviewerName + " gave you a " + rating + "-star review";
        String actionUrl = "/profile/reviews";
        
        createNotification(user, title, message, Notification.NotificationType.REVIEW_RECEIVED, actionUrl);
    }

    public void createBadgeEarnedNotification(User user, String badgeName) {
        String title = "Badge Earned!";
        String message = "Congratulations! You've earned the '" + badgeName + "' badge";
        String actionUrl = "/profile/badges";
        
        createNotification(user, title, message, Notification.NotificationType.BADGE_EARNED, actionUrl);
    }

    public void createSystemAnnouncement(String title, String message) {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            createNotification(user, title, message, Notification.NotificationType.SYSTEM_ANNOUNCEMENT, null);
        }
    }

    public Map<String, Boolean> getUserPreferences(User user) {
        Map<String, Boolean> preferences = new HashMap<>();
        
        // Default preferences
        preferences.put("projectApplications", true);
        preferences.put("projectUpdates", true);
        preferences.put("messages", true);
        preferences.put("reviews", true);
        preferences.put("badges", true);
        preferences.put("systemAnnouncements", true);
        preferences.put("emailNotifications", true);
        preferences.put("pushNotifications", true);
        
        return preferences;
    }

    public Map<String, Boolean> updateUserPreferences(User user, Map<String, Boolean> newPreferences) {
        // In a real application, you would save these to a user_preferences table
        // For now, we'll just return the updated preferences
        return newPreferences;
    }

    public List<Map<String, Object>> getNotificationTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        
        for (Notification.NotificationType type : Notification.NotificationType.values()) {
            Map<String, Object> typeInfo = new HashMap<>();
            typeInfo.put("type", type.name());
            typeInfo.put("displayName", getDisplayNameForType(type));
            typeInfo.put("description", getDescriptionForType(type));
            typeInfo.put("icon", getIconForType(type));
            types.add(typeInfo);
        }
        
        return types;
    }

    private String getIconForType(Notification.NotificationType type) {
        return switch (type) {
            case PROJECT_APPLICATION -> "📝";
            case PROJECT_ACCEPTED -> "✅";
            case PROJECT_REJECTED -> "❌";
            case PROJECT_COMPLETED -> "🎉";
            case NEW_MESSAGE -> "💬";
            case REVIEW_RECEIVED -> "⭐";
            case BADGE_EARNED -> "🏆";
            case SYSTEM_ANNOUNCEMENT -> "📢";
            case PAYMENT_RECEIVED -> "💰";
            case DEADLINE_REMINDER -> "⏰";
        };
    }

    private String getDisplayNameForType(Notification.NotificationType type) {
        return switch (type) {
            case PROJECT_APPLICATION -> "Project Application";
            case PROJECT_ACCEPTED -> "Application Accepted";
            case PROJECT_REJECTED -> "Application Rejected";
            case PROJECT_COMPLETED -> "Project Completed";
            case NEW_MESSAGE -> "New Message";
            case REVIEW_RECEIVED -> "Review Received";
            case BADGE_EARNED -> "Badge Earned";
            case SYSTEM_ANNOUNCEMENT -> "System Announcement";
            case PAYMENT_RECEIVED -> "Payment Received";
            case DEADLINE_REMINDER -> "Deadline Reminder";
        };
    }

    private String getDescriptionForType(Notification.NotificationType type) {
        return switch (type) {
            case PROJECT_APPLICATION -> "When someone applies for your project";
            case PROJECT_ACCEPTED -> "When your application is accepted";
            case PROJECT_REJECTED -> "When your application is rejected";
            case PROJECT_COMPLETED -> "When a project is completed";
            case NEW_MESSAGE -> "When you receive a new message";
            case REVIEW_RECEIVED -> "When you receive a review";
            case BADGE_EARNED -> "When you earn a new badge";
            case SYSTEM_ANNOUNCEMENT -> "Important platform announcements";
            case PAYMENT_RECEIVED -> "When you receive a payment";
            case DEADLINE_REMINDER -> "Project deadline reminders";
        };
    }
} 