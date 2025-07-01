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

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private com.realfreelancer.repository.UserRepository userRepository;

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

            Message message = new Message();
            message.setContent(messageRequest.getContent());
            message.setSender(sender);
            message.setReceiver(recipient);
            message.setType(Message.MessageType.TEXT);

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
} 