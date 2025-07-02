package com.realfreelancer.controller;

import com.realfreelancer.model.Message;
import com.realfreelancer.model.User;
import com.realfreelancer.service.ChatService;
import com.realfreelancer.dto.MessageRequest;
import com.realfreelancer.dto.ConversationSummary;
import com.realfreelancer.dto.MessageDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realfreelancer.repository.ProjectRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Header;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private com.realfreelancer.repository.UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Helper to robustly find user by username or email
    private Optional<User> findUserByUsernameOrEmail(String identifier) {
        Optional<User> user = userRepository.findByUsername(identifier);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(identifier);
        }
        return user;
    }

    // Send a message
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageRequest messageRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> senderOpt = findUserByUsernameOrEmail(username);
            if (senderOpt.isEmpty()) {
                logger.error("User not found for identifier: {}", username);
                return ResponseEntity.status(404).body("User not found");
            }
            User sender = senderOpt.get();

            Optional<User> recipientOpt = userRepository.findById(messageRequest.getRecipientId());
            if (!recipientOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            User recipient = recipientOpt.get();

            if (messageRequest.getProjectId() == null) {
                return ResponseEntity.badRequest().body("Project ID is required for sending a message");
            }
            Optional<com.realfreelancer.model.Project> projectOpt = projectRepository.findById(messageRequest.getProjectId());
            if (!projectOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Invalid project ID");
            }
            com.realfreelancer.model.Project project = projectOpt.get();

            Message message = new Message();
            message.setContent(messageRequest.getContent());
            message.setSender(sender);
            message.setReceiver(recipient);
            message.setType(Message.MessageType.TEXT);
            message.setProject(project);

            MessageDTO savedMessage = chatService.sendMessage(message);
            return ResponseEntity.ok(savedMessage);

        } catch (Exception e) {
            logger.error("Error sending message", e);
            return ResponseEntity.badRequest().body("Error sending message: " + e.getMessage());
        }
    }

    // Get conversation between two users
    @GetMapping("/conversation/{userId}")
    public ResponseEntity<?> getConversation(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> currentUserOpt = findUserByUsernameOrEmail(username);
            if (currentUserOpt.isEmpty()) {
                logger.error("User not found for identifier: {}", username);
                return ResponseEntity.status(404).body("User not found");
            }
            User currentUser = currentUserOpt.get();

            Optional<User> otherUserOpt = userRepository.findById(userId);
            if (!otherUserOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User otherUser = otherUserOpt.get();
            List<MessageDTO> messages = chatService.getConversation(currentUser, otherUser, page, size);
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            logger.error("Error fetching conversation", e);
            return ResponseEntity.badRequest().body("Error fetching conversation: " + e.getMessage());
        }
    }

    // Get user's conversations list
    @GetMapping("/conversations")
    public ResponseEntity<?> getUserConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> userOpt = findUserByUsernameOrEmail(username);
            if (userOpt.isEmpty()) {
                logger.error("User not found for identifier: {}", username);
                return ResponseEntity.status(404).body("User not found");
            }
            User user = userOpt.get();

            List<ConversationSummary> conversations = chatService.getUserConversations(user, page, size);
            return ResponseEntity.ok(conversations);

        } catch (Exception e) {
            logger.error("Error fetching conversations", e);
            return ResponseEntity.badRequest().body("Error fetching conversations: " + e.getMessage());
        }
    }

    // Mark messages as read
    @PutMapping("/read/{senderId}")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable Long senderId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> currentUserOpt = findUserByUsernameOrEmail(username);
            if (currentUserOpt.isEmpty()) {
                logger.error("User not found for identifier: {}", username);
                return ResponseEntity.status(404).body("User not found");
            }
            User currentUser = currentUserOpt.get();

            Optional<User> senderOpt = userRepository.findById(senderId);
            if (!senderOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User sender = senderOpt.get();
            chatService.markMessagesAsRead(sender, currentUser);
            return ResponseEntity.ok(Map.of("updated", true));

        } catch (Exception e) {
            logger.error("Error marking messages as read", e);
            return ResponseEntity.badRequest().body("Error marking messages as read: " + e.getMessage());
        }
    }

    // Get unread message count
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadMessageCount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> userOpt = findUserByUsernameOrEmail(username);
            if (userOpt.isEmpty()) {
                logger.error("User not found for identifier: {}", username);
                return ResponseEntity.status(404).body("User not found");
            }
            User user = userOpt.get();

            Long unreadCount = chatService.getUnreadMessageCount(user);
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));

        } catch (Exception e) {
            logger.error("Error fetching unread count", e);
            return ResponseEntity.badRequest().body("Error fetching unread count: " + e.getMessage());
        }
    }

    // Delete a message
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> userOpt = findUserByUsernameOrEmail(username);
            if (userOpt.isEmpty()) {
                logger.error("User not found for identifier: {}", username);
                return ResponseEntity.status(404).body("User not found");
            }
            User user = userOpt.get();
            chatService.deleteMessage(messageId, user);
            return ResponseEntity.ok("Message deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting message", e);
            return ResponseEntity.badRequest().body("Error deleting message: " + e.getMessage());
        }
    }

    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingIndicatorDTO typingIndicator, @Header("simpUser") Authentication auth) {
        System.out.println("Received typing event: " + typingIndicator.getSenderUsername() + " -> " + typingIndicator.getReceiverUsername() + " typing: " + typingIndicator.isTyping());
        System.out.println("WebSocket principal: " + auth.getName());
        messagingTemplate.convertAndSendToUser(
            typingIndicator.getReceiverUsername(),
            "/queue/typing",
            typingIndicator
        );
    }
}

// DTO for typing indicator
class TypingIndicatorDTO {
    private Long senderId;
    private String senderUsername;
    private Long receiverId;
    private String receiverUsername;
    private boolean typing;
    // Getters and setters
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getReceiverUsername() { return receiverUsername; }
    public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }
    public boolean isTyping() { return typing; }
    public void setTyping(boolean typing) { this.typing = typing; }
} 