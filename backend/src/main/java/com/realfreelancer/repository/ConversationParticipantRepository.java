package com.realfreelancer.repository;

import com.realfreelancer.model.ConversationParticipant;
import com.realfreelancer.model.Conversation;
import com.realfreelancer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    List<ConversationParticipant> findByConversation(Conversation conversation);
    List<ConversationParticipant> findByUser(User user);
} 