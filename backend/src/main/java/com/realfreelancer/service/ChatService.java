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
import java.util.stream.Collectors;
import com.realfreelancer.dto.ConversationSummary;
import com.realfreelancer.dto.MessageDTO;
import com.realfreelancer.model.Project;

@Service
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public MessageDTO sendMessage(Message message) {
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
            toDTO(savedMessage)
        );
        
        return toDTO(savedMessage);
    }

    public List<MessageDTO> getConversation(User user1, User user2, int page, int size) {
        return messageRepository.findConversationBetweenUsers(user1, user2, 
            org.springframework.data.domain.PageRequest.of(page, size, 
                org.springframework.data.domain.Sort.by("createdAt").descending())
        ).getContent().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void markMessagesAsRead(User sender, User receiver) {
        messageRepository.markMessagesAsRead(sender, receiver);
    }

    public Long getUnreadMessageCount(User user) {
        return messageRepository.countUnreadMessagesByReceiver(user);
    }

    public List<ConversationSummary> getUserConversations(User user, int page, int size) {
        List<Object[]> raw = messageRepository.findUserConversationsNative(user.getId());
        return raw.stream()
            .skip((long) page * size)
            .limit(size)
            .map(obj -> new ConversationSummary(
                ((Number)obj[0]).longValue(), // userId
                (String)obj[1], // username
                (String)obj[2], // avatarUrl
                obj[3] != null ? ((java.sql.Timestamp)obj[3]).toLocalDateTime() : null, // lastMessageTime
                obj[4] != null ? ((Number)obj[4]).longValue() : 0L // unreadCount
            ))
            .collect(Collectors.toList());
    }

    public void deleteMessage(Long messageId, User user) {
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own messages");
        }
        
        messageRepository.delete(message);
    }

    public void sendSystemMessage(User sender, User receiver, String content, Project project) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(Message.MessageType.SYSTEM);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        message.setProject(project);
        messageRepository.save(message);
    }

    // Helper to map Message to MessageDTO
    private MessageDTO toDTO(Message message) {
        Long projectId = (message.getProject() != null) ? message.getProject().getId() : null;
        return new MessageDTO(
            message.getId(),
            message.getContent(),
            message.getSender().getId(),
            message.getSender().getUsername(),
            message.getReceiver().getId(),
            message.getReceiver().getUsername(),
            message.getIsRead(),
            message.getAttachmentUrl(),
            message.getType().name(),
            message.getCreatedAt(),
            projectId
        );
    }
} 