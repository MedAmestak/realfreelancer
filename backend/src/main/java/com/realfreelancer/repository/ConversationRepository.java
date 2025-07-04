package com.realfreelancer.repository;

import com.realfreelancer.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c JOIN c.participants p1 JOIN c.participants p2 WHERE c.type = 'PRIVATE' AND p1.user.id = :user1Id AND p2.user.id = :user2Id AND SIZE(c.participants) = 2")
    List<Conversation> findPrivateConversationBetweenUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
} 