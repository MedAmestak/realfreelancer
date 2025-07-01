package com.realfreelancer.repository;

import com.realfreelancer.model.Message;
import com.realfreelancer.model.User;
import com.realfreelancer.dto.ConversationSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Find conversation between two users (paginated)
    @Query("SELECT m FROM Message m WHERE ((m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1)) ORDER BY m.createdAt DESC")
    Page<Message> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2, Pageable pageable);

    // Mark messages as read between two users
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.sender = :sender AND m.receiver = :receiver AND m.isRead = false")
    int markMessagesAsRead(@Param("sender") User sender, @Param("receiver") User receiver);

    // Find user conversations (list of users they've chatted with) - native query for PostgreSQL
    @Query(value = "SELECT " +
        "u.id AS user_id, " +
        "u.username, " +
        "u.avatar_url, " +
        "m.last_message_time, " +
        "m.unread_count " +
        "FROM ( " +
        "  SELECT " +
        "    CASE WHEN sender_id = :userId THEN receiver_id ELSE sender_id END AS partner_id, " +
        "    MAX(created_at) AS last_message_time, " +
        "    COUNT(CASE WHEN receiver_id = :userId AND is_read = false THEN 1 END) AS unread_count " +
        "  FROM messages " +
        "  WHERE sender_id = :userId OR receiver_id = :userId " +
        "  GROUP BY partner_id " +
        ") m " +
        "JOIN users u ON u.id = m.partner_id " +
        "ORDER BY m.last_message_time DESC",
        nativeQuery = true)
    List<Object[]> findUserConversationsNative(@Param("userId") Long userId);

    // Count unread messages for a user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :user AND m.isRead = false")
    Long countUnreadMessagesByReceiver(@Param("user") User user);
} 