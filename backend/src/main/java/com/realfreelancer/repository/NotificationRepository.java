package com.realfreelancer.repository;

import com.realfreelancer.model.Notification;
import com.realfreelancer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByUser(User user, Pageable pageable);
    
    Page<Notification> findByUserAndType(User user, Notification.NotificationType type, Pageable pageable);
    
    Long countByUserAndIsReadFalse(User user);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user = :user AND n.isRead = false")
    int markAllAsRead(@Param("user") User user);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.type = :type AND n.isRead = false")
    Long countUnreadByUserAndType(@Param("user") User user, @Param("type") Notification.NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false ORDER BY n.createdAt DESC")
    Page<Notification> findUnreadByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    Page<Notification> findRecentByUser(@Param("user") User user, @Param("since") java.time.LocalDateTime since, Pageable pageable);
} 