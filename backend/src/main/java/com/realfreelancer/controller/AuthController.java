package com.realfreelancer.controller;

import com.realfreelancer.config.jwt.JwtTokenProvider;
import com.realfreelancer.dto.AuthRequest;
import com.realfreelancer.dto.AuthResponse;
import com.realfreelancer.model.User;
import com.realfreelancer.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.realfreelancer.service.AnalyticsService;
import java.util.Map;

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

    @Autowired
    private AnalyticsService analyticsService;

    @Value("${jwt.refresh-cookie-name}")
    private String refreshTokenCookieName;

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
            // authResponse.setExpiresAt(LocalDateTime.ofInstant(jwtTokenProvider.getExpirationDateFromToken(token).toInstant(), java.time.ZoneId.systemDefault()));

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
    public ResponseEntity<?> authenticateUser(@Validated(AuthRequest.Login.class) @RequestBody AuthRequest authRequest, HttpServletResponse response) {
        logger.info("Authenticating user with email: {}", authRequest.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails.getUsername());

            User user = userService.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authRequest.getEmail()));
            
            Cookie refreshTokenCookie = new Cookie(refreshTokenCookieName, refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/api/auth");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            // refreshTokenCookie.setSecure(true); // true in production
            response.addCookie(refreshTokenCookie);

            AuthResponse authResponse = new AuthResponse(accessToken, user.getId(), user.getUsername(), user.getEmail());
            authResponse.setGithubLink(user.getGithubLink());
            authResponse.setReputationPoints(user.getReputationPoints());
            authResponse.setExpiresAt(LocalDateTime.ofInstant(jwtTokenProvider.getExpirationDateFromToken(accessToken).toInstant(), java.time.ZoneId.systemDefault()));
            return ResponseEntity.ok(authResponse);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of("message", "User not found"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("message", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of("message", "An error occurred during login"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found.");
        }

        try {
            if (jwtTokenProvider.validateToken(refreshToken)) {
                String email = jwtTokenProvider.getUsernameFromToken(refreshToken);
                User user = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
                String newAccessToken = jwtTokenProvider.generateToken(email);

                AuthResponse authResponse = new AuthResponse(newAccessToken, user.getId(), user.getUsername(), user.getEmail());
                authResponse.setExpiresAt(LocalDateTime.ofInstant(jwtTokenProvider.getExpirationDateFromToken(newAccessToken).toInstant(), java.time.ZoneId.systemDefault()));
                
                return ResponseEntity.ok(authResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during token refresh.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(refreshTokenCookieName, null);
        cookie.setPath("/api/auth");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // Expires immediately
        response.addCookie(cookie);
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
            }
    
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
            Map<String, Object> stats = analyticsService.getUserProfileStats(user);
    
            AuthUserDTO dto = new AuthUserDTO(user, stats);
            return ResponseEntity.ok(dto);
        } catch (UsernameNotFoundException e) {
            System.err.println("User not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (Exception e) {
            e.printStackTrace();  // Print full stack trace for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Unexpected error: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUser(@RequestBody java.util.Map<String, Object> updates) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Update allowed fields
            if (updates.containsKey("username")) {
                user.setUsername((String) updates.get("username"));
            }
            if (updates.containsKey("bio")) {
                user.setBio((String) updates.get("bio"));
            }
            if (updates.containsKey("githubLink")) {
                user.setGithubLink((String) updates.get("githubLink"));
            }
            if (updates.containsKey("skills")) {
                Object skillsObj = updates.get("skills");
                if (skillsObj instanceof java.util.List) {
                    java.util.List<?> skillsList = (java.util.List<?>) skillsObj;
                    java.util.Set<String> skillsSet = new java.util.HashSet<>();
                    for (Object skill : skillsList) {
                        if (skill instanceof String) {
                            skillsSet.add((String) skill);
                        }
                    }
                    user.setSkills(skillsSet);
                }
            }
            userService.updateUser(user);
            java.util.Map<String, Object> stats = analyticsService.getUserProfileStats(user);
            return ResponseEntity.ok(new AuthUserDTO(user, stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user profile: " + e.getMessage());
        }
    }
}