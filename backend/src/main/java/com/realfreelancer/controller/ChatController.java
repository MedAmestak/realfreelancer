package com.realfreelancer.controller;

import com.realfreelancer.model.Message;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.MessageRepository;
import com.realfreelancer.repository.UserRepository;
import com.realfreelancer.service.ChatService;
import com.realfreelancer.dto.MessageRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // Send a message
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody MessageRequest messageRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User sender = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

            Message savedMessage = chatService.sendMessage(message);
            return ResponseEntity.ok(savedMessage);

        } catch (Exception e) {
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
            User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<User> otherUserOpt = userRepository.findById(userId);
            if (!otherUserOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User otherUser = otherUserOpt.get();
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            
            Page<Message> messages = messageRepository.findConversationBetweenUsers(
                currentUser, otherUser, pageable
            );

            return ResponseEntity.ok(messages);

        } catch (Exception e) {
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
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<Object[]> conversations = messageRepository.findUserConversations(user, pageable);

            return ResponseEntity.ok(conversations);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching conversations: " + e.getMessage());
        }
    }

    // Mark messages as read
    @PutMapping("/read/{senderId}")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable Long senderId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<User> senderOpt = userRepository.findById(senderId);
            if (!senderOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User sender = senderOpt.get();
            int updatedCount = messageRepository.markMessagesAsRead(sender, currentUser);

            return ResponseEntity.ok(Map.of("updatedCount", updatedCount));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error marking messages as read: " + e.getMessage());
        }
    }

    // Get unread message count
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadMessageCount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Long unreadCount = messageRepository.countUnreadMessagesByReceiver(user);
            return ResponseEntity.ok(Map.of("unreadCount", unreadCount));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching unread count: " + e.getMessage());
        }
    }

    // Delete a message
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<Message> messageOpt = messageRepository.findById(messageId);
            if (!messageOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Message message = messageOpt.get();
            if (!message.getSender().getUsername().equals(username)) {
                return ResponseEntity.status(403).body("You can only delete your own messages");
            }

            messageRepository.delete(message);
            return ResponseEntity.ok("Message deleted successfully");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting message: " + e.getMessage());
        }
    }

    // WebSocket message handlers
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public Message sendWebSocketMessage(@Payload MessageRequest messageRequest) {
        // This will be handled by WebSocket configuration
        return null;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public Message addUser(@Payload MessageRequest messageRequest, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", messageRequest.getSenderId());
        return null;
    }
} 