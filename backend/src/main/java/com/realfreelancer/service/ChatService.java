package com.realfreelancer.service;

import com.realfreelancer.model.Message;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.MessageRepository;
import com.realfreelancer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Message sendMessage(Message message) {
        // Set timestamp
        message.setCreatedAt(LocalDateTime.now());
        message.setIsRead(false);
        
        // Save message
        Message savedMessage = messageRepository.save(message);
        
        // Send real-time notification to recipient
        String recipientUsername = message.getReceiver().getUsername();
        messagingTemplate.convertAndSendToUser(
            recipientUsername,
            "/queue/messages",
            savedMessage
        );
        
        return savedMessage;
    }

    public List<Message> getConversation(User user1, User user2, int page, int size) {
        return messageRepository.findConversationBetweenUsers(user1, user2, 
            org.springframework.data.domain.PageRequest.of(page, size, 
                org.springframework.data.domain.Sort.by("createdAt").descending())
        ).getContent();
    }

    public void markMessagesAsRead(User sender, User receiver) {
        messageRepository.markMessagesAsRead(sender, receiver);
    }

    public Long getUnreadMessageCount(User user) {
        return messageRepository.countUnreadMessagesByReceiver(user);
    }

    public List<Object[]> getUserConversations(User user) {
        return messageRepository.findUserConversations(user, 
            org.springframework.data.domain.PageRequest.of(0, 20, 
                org.springframework.data.domain.Sort.by("createdAt").descending())
        );
    }

    public void deleteMessage(Long messageId, User user) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own messages");
        }
        
        messageRepository.delete(message);
    }
} 