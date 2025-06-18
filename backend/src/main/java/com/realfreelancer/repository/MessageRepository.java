package com.realfreelancer.repository;

import com.realfreelancer.model.Message;
import com.realfreelancer.model.Project;
import com.realfreelancer.model.User;
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
    
    Page<Message> findByProjectOrderByCreatedAtDesc(Project project, Pageable pageable);
    
    List<Message> findByProjectAndSenderAndReceiver(Project project, User sender, User receiver);
    
    // Find conversation between two users (without project context)
    @Query("SELECT m FROM Message m WHERE " +
           "((m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1)) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversationBetweenUsers(@Param("user1") User user1, 
                                              @Param("user2") User user2, 
                                              Pageable pageable);
    
    // Mark messages as read between two users
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.sender = :sender AND m.receiver = :receiver AND m.isRead = false")
    int markMessagesAsRead(@Param("sender") User sender, @Param("receiver") User receiver);
    
    // Find user conversations (list of users they've chatted with)
    @Query("SELECT DISTINCT " +
           "CASE WHEN m.sender = :user THEN m.receiver ELSE m.sender END as otherUser, " +
           "MAX(m.createdAt) as lastMessageTime, " +
           "COUNT(CASE WHEN m.receiver = :user AND m.isRead = false THEN 1 END) as unreadCount " +
           "FROM Message m " +
           "WHERE m.sender = :user OR m.receiver = :user " +
           "GROUP BY otherUser " +
           "ORDER BY lastMessageTime DESC")
    List<Object[]> findUserConversations(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.project = :project AND " +
           "((m.sender = :user1 AND m.receiver = :user2) OR " +
           "(m.sender = :user2 AND m.receiver = :user1)) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("project") Project project, 
                                  @Param("user1") User user1, 
                                  @Param("user2") User user2);
    
    @Query("SELECT m FROM Message m WHERE m.receiver = :user AND m.isRead = false ORDER BY m.createdAt DESC")
    List<Message> findUnreadMessagesByReceiver(@Param("user") User user);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :user AND m.isRead = false")
    Long countUnreadMessagesByReceiver(@Param("user") User user);
    
    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiver = :user AND m.project = :project")
    void markMessagesAsRead(@Param("user") User user, @Param("project") Project project);
    
    @Query("SELECT DISTINCT m.project FROM Message m WHERE m.sender = :user OR m.receiver = :user")
    List<Project> findProjectsWithMessages(@Param("user") User user);
    
    @Query("SELECT m FROM Message m WHERE m.project = :project ORDER BY m.createdAt DESC LIMIT 1")
    Message findLatestMessageByProject(@Param("project") Project project);
} 