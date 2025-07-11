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
import com.realfreelancer.model.Conversation;
import com.realfreelancer.model.ConversationParticipant;
import com.realfreelancer.repository.ConversationRepository;
import com.realfreelancer.repository.ConversationParticipantRepository;
import com.realfreelancer.service.NotificationService;

@Service
public class ChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private ConversationParticipantRepository conversationParticipantRepository;

    @Autowired
    private NotificationService notificationService;

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
        // Create notification for new message
        notificationService.createNewMessageNotification(message.getReceiver(), message.getSender().getUsername());
        
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
                null, // conversationId not available in this legacy method
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

    public void sendSystemMessage(User sender, User receiver, String content, Conversation conversation) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setType(Message.MessageType.SYSTEM);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        message.setConversation(conversation);
        messageRepository.save(message);
    }

    public Conversation findOrCreatePrivateConversation(User user1, User user2) {
        // Try to find existing PRIVATE conversation with exactly these two users
        List<Conversation> existing = conversationRepository.findPrivateConversationBetweenUsers(user1.getId(), user2.getId());
        if (!existing.isEmpty()) {
            return existing.get(0);
        }
        // Create new conversation
        Conversation conversation = new Conversation(Conversation.ConversationType.PRIVATE);
        conversation = conversationRepository.save(conversation);
        conversationParticipantRepository.save(new ConversationParticipant(conversation, user1));
        conversationParticipantRepository.save(new ConversationParticipant(conversation, user2));
        return conversation;
    }

    // Helper to map Message to MessageDTO
    private MessageDTO toDTO(Message message) {
        Long conversationId = (message.getConversation() != null) ? message.getConversation().getId() : null;
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
            conversationId
        );
    }

    // Find conversation by ID 
    public java.util.Optional<Conversation> findConversationById(Long conversationId) {
        return conversationRepository.findById(conversationId);
    }

    // Find the other participant in a PRIVATE conversation
    public User findOtherParticipant(Conversation conversation, User sender) {
        return conversation.getParticipants().stream()
            .map(com.realfreelancer.model.ConversationParticipant::getUser)
            .filter(u -> !u.getId().equals(sender.getId()))
            .findFirst().orElse(null);
    }

    // Check if a user is a participant in a conversation
    public boolean isParticipant(Conversation conversation, User user) {
        return conversation.getParticipants().stream()
            .anyMatch(p -> p.getUser().getId().equals(user.getId()));
    }

    // Get messages by conversation
    public List<MessageDTO> getMessagesByConversation(Conversation conversation, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        return messageRepository.findAllByConversation(conversation, pageable)
            .getContent().stream().map(this::toDTO).collect(java.util.stream.Collectors.toList());
    }

    public void markMessagesAsReadByConversation(Conversation conversation, User user) {
        List<Message> unread = messageRepository.findAllByConversationAndReceiverAndIsReadFalse(conversation, user);
        for (Message m : unread) {
            m.setIsRead(true);
        }
        messageRepository.saveAll(unread);
    }

    public List<ConversationSummary> getUserConversationsWithId(User user, int page, int size) {
        List<ConversationParticipant> participants = conversationParticipantRepository.findByUser(user);
        return participants.stream()
            .map(cp -> {
                Conversation c = cp.getConversation();
                User other = c.getParticipants().stream()
                    .map(com.realfreelancer.model.ConversationParticipant::getUser)
                    .filter(u -> !u.getId().equals(user.getId()))
                    .findFirst().orElse(null);
                String username = (other != null) ? other.getUsername() : "Unknown";
                String avatarUrl = (other != null) ? other.getAvatarUrl() : null;
                java.time.LocalDateTime lastMessageTime = c.getMessages().stream()
                    .map(Message::getCreatedAt)
                    .max(java.time.LocalDateTime::compareTo).orElse(null);
                long unreadCount = c.getMessages().stream()
                    .filter(m -> m.getReceiver().getId().equals(user.getId()) && !Boolean.TRUE.equals(m.getIsRead()))
                    .count();
                return new ConversationSummary(
                    c.getId(), // conversationId
                    (other != null) ? other.getId() : null, // userId
                    username,
                    avatarUrl,
                    lastMessageTime,
                    unreadCount
                );
            })
            .sorted((a, b) -> {
                if (a.getLastMessageTime() == null && b.getLastMessageTime() == null) return 0;
                if (a.getLastMessageTime() == null) return 1;
                if (b.getLastMessageTime() == null) return -1;
                return b.getLastMessageTime().compareTo(a.getLastMessageTime());
            })
            .skip((long) page * size)
            .limit(size)
            .collect(java.util.stream.Collectors.toList());
    }
} 