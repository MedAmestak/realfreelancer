package com.realfreelancer.controller;

import com.realfreelancer.model.Notification;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.NotificationRepository;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Helper method to find user by username or email
    private User findUserByPrincipal(String principal) {
        return userRepository.findByUsername(principal)
            .or(() -> userRepository.findByEmail(principal))
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get user's notifications
    @GetMapping
    public ResponseEntity<?> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = authentication.getName();
            User user = findUserByPrincipal(principal);

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Notification> notifications;
            
            if (type != null && !type.trim().isEmpty()) {
                notifications = notificationRepository.findByUserAndType(user, 
                    Notification.NotificationType.valueOf(type.toUpperCase()), pageable);
            } else {
                notifications = notificationRepository.findByUser(user, pageable);
            }

            // Map to DTOs
            List<Map<String, ?>> dtos = notifications.getContent().stream().map(n -> Map.of(
                "id", n.getId(),
                "type", n.getType().name(),
                "title", n.getTitle(),
                "message", n.getMessage(),
                "isRead", n.getIsRead(),
                "createdAt", n.getCreatedAt(),
                "link", n.getActionUrl()
            )).collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching notifications: " + e.getMessage());
        }
    }

    // Get unread notification count
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = authentication.getName();
            User user = findUserByPrincipal(principal);

            Long unreadCount = notificationRepository.countByUserAndIsReadFalse(user);
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching unread count: " + e.getMessage());
        }
    }

    // Mark notification as read
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = authentication.getName();
            User user = findUserByPrincipal(principal);

            Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
            if (!notificationOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Notification notification = notificationOpt.get();
            if (!notification.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body("You can only mark your own notifications as read");
            }

            notification.setIsRead(true);
            Notification updatedNotification = notificationRepository.save(notification);
            return ResponseEntity.ok(updatedNotification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking notification as read: " + e.getMessage());
        }
    }

    // Mark all notifications as read
    @PutMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = authentication.getName();
            User user = findUserByPrincipal(principal);

            int updatedCount = notificationRepository.markAllAsRead(user);
            return ResponseEntity.ok(Map.of("updatedCount", updatedCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking all notifications as read: " + e.getMessage());
        }
    }

    // Delete notification
    // @DeleteMapping("/{notificationId}")
    // public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
    //     try {
    //         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //         String username = authentication.getName();
    //         User user = userRepository.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("User not found"));

    //         Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
    //         if (!notificationOpt.isPresent()) {
    //             return ResponseEntity.notFound().build();
    //         }

    //         Notification notification = notificationOpt.get();
    //         if (!notification.getUser().getId().equals(user.getId())) {
    //             return ResponseEntity.status(403).body("You can only delete your own notifications");
    //         }

    //         notificationRepository.delete(notification);
    //         return ResponseEntity.ok("Notification deleted successfully");
    //     } catch (Exception e) {
    //         return ResponseEntity.badRequest().body("Error deleting notification: " + e.getMessage());
    //     }
    // }

    // Get notification preferences
    @GetMapping("/preferences")
    public ResponseEntity<?> getNotificationPreferences() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = authentication.getName();
            User user = findUserByPrincipal(principal);

            Map<String, Boolean> preferences = notificationService.getUserPreferences(user);
            return ResponseEntity.ok(preferences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching preferences: " + e.getMessage());
        }
    }

    // Update notification preferences
    @PutMapping("/preferences")
    public ResponseEntity<?> updateNotificationPreferences(@RequestBody Map<String, Boolean> preferences) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String principal = authentication.getName();
            User user = findUserByPrincipal(principal);

            Map<String, Boolean> updatedPreferences = notificationService.updateUserPreferences(user, preferences);
            return ResponseEntity.ok(updatedPreferences);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating preferences: " + e.getMessage());
        }
    }

    // Get notification types
    @GetMapping("/types")
    public ResponseEntity<?> getNotificationTypes() {
        try {
            List<Map<String, Object>> types = notificationService.getNotificationTypes();
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching notification types: " + e.getMessage());
        }
    }
} 