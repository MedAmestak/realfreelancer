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
import com.realfreelancer.model.Conversation;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;

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

    // Send a message (now using conversationId)
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageRequest messageRequest) {
        logger.info("/api/chat/send payload: {}", messageRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> senderOpt = findUserByUsernameOrEmail(username);
        if (senderOpt.isEmpty()) {
            logger.error("User not found for identifier: {}", username);
            return ResponseEntity.status(404).body("User not found");
        }
        User sender = senderOpt.get();

        if (messageRequest.getConversationId() == null) {
            logger.error("conversationId is missing in payload: {}", messageRequest);
            return ResponseEntity.badRequest().body("conversationId is required");
        }
        Optional<Conversation> conversationOpt = chatService.findConversationById(messageRequest.getConversationId());
        if (conversationOpt.isEmpty()) {
            logger.error("Conversation not found for id: {}", messageRequest.getConversationId());
            return ResponseEntity.status(404).body("Conversation not found");
        }
        Conversation conversation = conversationOpt.get();

        // Find the other participant
        User receiver = chatService.findOtherParticipant(conversation, sender);
        if (receiver == null) {
            logger.error("Invalid conversation participants for conversationId: {}", messageRequest.getConversationId());
            return ResponseEntity.badRequest().body("Invalid conversation participants");
        }

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setType(Message.MessageType.TEXT);
        message.setConversation(conversation);

        MessageDTO savedMessage;
        try {
            savedMessage = chatService.sendMessage(message);
        } catch (Exception e) {
            logger.error("Error saving message. Payload: {}. Exception: ", messageRequest, e);
            return ResponseEntity.badRequest().body("Error saving message: " + e.getMessage());
        }

        // Post-processing: notifications, WebSocket, etc. (do not affect response)
        try {
            // Already handled in chatService.sendMessage, but if you add more, do it here
        } catch (Exception e) {
            logger.error("Post-processing error after sending message: ", e);
        }

        return ResponseEntity.ok(savedMessage);
    }

    // Get messages in a conversation
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<?> getConversation(
            @PathVariable Long conversationId,
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

            Optional<Conversation> conversationOpt = chatService.findConversationById(conversationId);
            if (conversationOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Conversation not found");
            }
            Conversation conversation = conversationOpt.get();

            if (!chatService.isParticipant(conversation, currentUser)) {
                return ResponseEntity.status(403).body("Not a participant in this conversation");
            }

            List<MessageDTO> messages = chatService.getMessagesByConversation(conversation, page, size);
            return ResponseEntity.ok(messages);

        } catch (Exception e) {
            logger.error("Error fetching conversation", e);
            return ResponseEntity.badRequest().body("Error fetching conversation: " + e.getMessage());
        }
    }

    // Mark all messages as read in a conversation for the current user
    @PutMapping("/read/{conversationId}")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable Long conversationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> currentUserOpt = findUserByUsernameOrEmail(username);
            if (currentUserOpt.isEmpty()) {
                logger.error("User not found for identifier: {}", username);
                return ResponseEntity.status(404).body("User not found");
            }
            User currentUser = currentUserOpt.get();

            Optional<Conversation> conversationOpt = chatService.findConversationById(conversationId);
            if (conversationOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Conversation not found");
            }
            Conversation conversation = conversationOpt.get();

            if (!chatService.isParticipant(conversation, currentUser)) {
                return ResponseEntity.status(403).body("Not a participant in this conversation");
            }

            chatService.markMessagesAsReadByConversation(conversation, currentUser);
            return ResponseEntity.ok(Map.of("updated", true));

        } catch (Exception e) {
            logger.error("Error marking messages as read", e);
            return ResponseEntity.badRequest().body("Error marking messages as read: " + e.getMessage());
        }
    }

    // Get user's conversations list (now returns conversationId)
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

            List<ConversationSummary> conversations = chatService.getUserConversationsWithId(user, page, size);
            return ResponseEntity.ok(conversations);

        } catch (Exception e) {
            logger.error("Error fetching conversations", e);
            return ResponseEntity.badRequest().body("Error fetching conversations: " + e.getMessage());
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

    // Create or fetch a conversation between the current user and another user (optionally for a project)
    @PostMapping("/conversation-with")
    public ResponseEntity<?> getOrCreateConversationWith(@RequestBody Map<String, Object> payload) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<User> currentUserOpt = findUserByUsernameOrEmail(username);
            if (currentUserOpt.isEmpty()) {
                logger.error("User not found for identifier: {}", username);
                return ResponseEntity.status(404).body("User not found");
            }
            User currentUser = currentUserOpt.get();

            Long userId = payload.get("userId") instanceof Number ? ((Number) payload.get("userId")).longValue() : null;
            if (userId == null) {
                return ResponseEntity.badRequest().body("userId is required");
            }
            Optional<User> otherUserOpt = userRepository.findById(userId);
            if (otherUserOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Other user not found");
            }
            User otherUser = otherUserOpt.get();

            // Optionally handle projectId if needed in the future
            // Long projectId = payload.get("projectId") instanceof Number ? ((Number) payload.get("projectId")).longValue() : null;

            Conversation conversation = chatService.findOrCreatePrivateConversation(currentUser, otherUser);
            return ResponseEntity.ok(Map.of("conversationId", conversation.getId()));
        } catch (Exception e) {
            logger.error("Error fetching/creating conversation", e);
            return ResponseEntity.badRequest().body("Error fetching/creating conversation: " + e.getMessage());
        }
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

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getDefaultMessage())
            .findFirst().orElse("Validation error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
} 