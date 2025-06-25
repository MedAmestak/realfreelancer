package com.realfreelancer.controller;

import com.realfreelancer.config.jwt.JwtTokenProvider;
import com.realfreelancer.dto.AuthRequest;
import com.realfreelancer.dto.AuthResponse;
import com.realfreelancer.model.User;
import com.realfreelancer.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.realfreelancer.dto.AuthUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated(AuthRequest.Registration.class) @RequestBody AuthRequest authRequest) {
        logger.info("Registering user with email: {}", authRequest.getEmail());
        try {
            User savedUser = userService.registerUser(authRequest);
            logger.info("User {} registered successfully with ID: {}", savedUser.getUsername(), savedUser.getId());

            final String token = jwtTokenProvider.generateToken(savedUser.getEmail());

            AuthResponse authResponse = new AuthResponse(token, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
            authResponse.setGithubLink(savedUser.getGithubLink());
            authResponse.setReputationPoints(savedUser.getReputationPoints());
            authResponse.setExpiresAt(LocalDateTime.ofInstant(jwtTokenProvider.getExpirationDateFromToken(token).toInstant(), java.time.ZoneId.systemDefault()));

            logger.info("Token generated for user {}.", savedUser.getUsername());
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed for email {}: {}", authRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Exception during user registration for email: {}", authRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred during registration.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Validated(AuthRequest.Login.class) @RequestBody AuthRequest authRequest) {
        logger.info("Authenticating user with email: {}", authRequest.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails.getUsername());

            User user = userService.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authRequest.getEmail()));

            AuthResponse authResponse = new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
            authResponse.setGithubLink(user.getGithubLink());
            authResponse.setReputationPoints(user.getReputationPoints());
            authResponse.setExpiresAt(LocalDateTime.ofInstant(jwtTokenProvider.getExpirationDateFromToken(token).toInstant(), java.time.ZoneId.systemDefault()));
            return ResponseEntity.ok(authResponse);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(new AuthUserDTO(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching user profile: " + e.getMessage());
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            if (jwtTokenProvider.validateToken(jwt)) {
                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.ok(false);
    }
}