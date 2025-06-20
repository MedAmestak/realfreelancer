package com.realfreelancer.controller;

import com.realfreelancer.config.jwt.JwtTokenProvider;
import com.realfreelancer.dto.AuthRequest;
import com.realfreelancer.dto.AuthResponse;
import com.realfreelancer.model.User;
import com.realfreelancer.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated(AuthRequest.Registration.class) @RequestBody AuthRequest authRequest) {
        try {
            // Check if username or email already exists
            if (userService.existsByUsername(authRequest.getUsername())) {
                return ResponseEntity.badRequest().body("Username is already taken!");
            }

            if (userService.existsByEmail(authRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Email is already in use!");
            }

            // Create new user
            User user = new User();
            user.setUsername(authRequest.getUsername());
            user.setEmail(authRequest.getEmail());
            user.setPassword(authRequest.getPassword()); // Will be encoded in service
            user.setGithubLink(authRequest.getGithubLink());
            user.setSkills(authRequest.getSkills());
            user.setBio(authRequest.getBio());

            User savedUser = userService.createUser(user);

            // Generate JWT token
            String token = jwtTokenProvider.generateToken(savedUser.getUsername());
            Date expiration = jwtTokenProvider.getExpirationDateFromToken(token);

            AuthResponse authResponse = new AuthResponse(token, savedUser.getId(), 
                savedUser.getUsername(), savedUser.getEmail());
            authResponse.setGithubLink(savedUser.getGithubLink());
            authResponse.setReputationPoints(savedUser.getReputationPoints());
            authResponse.setExpiresAt(LocalDateTime.ofInstant(expiration.toInstant(), 
                java.time.ZoneId.systemDefault()));

            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: Invalid data provided");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // Find user by email
            User user = userService.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authRequest.getEmail()));

            // Create authentication token with email
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(), // Use email for authentication
                    authRequest.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(user.getEmail()); // Use email for token
            Date expiration = jwtTokenProvider.getExpirationDateFromToken(token);

            AuthResponse authResponse = new AuthResponse(token, user.getId(), 
                user.getUsername(), user.getEmail());
            authResponse.setGithubLink(user.getGithubLink());
            authResponse.setReputationPoints(user.getReputationPoints());
            authResponse.setExpiresAt(LocalDateTime.ofInstant(expiration.toInstant(), 
                java.time.ZoneId.systemDefault()));

            return ResponseEntity.ok(authResponse);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred during login");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get user profile: Authentication error");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwt = token.substring(7);
                if (jwtTokenProvider.validateToken(jwt)) {
                    return ResponseEntity.ok("Token is valid");
                }
            }
            return ResponseEntity.badRequest().body("Invalid token");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token validation failed");
        }
    }
} 