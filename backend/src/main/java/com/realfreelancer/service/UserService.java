package com.realfreelancer.service;

import com.realfreelancer.dto.AuthRequest;
import com.realfreelancer.model.User;
import com.realfreelancer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.HashSet;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public User registerUser(AuthRequest authRequest) {
        if (userRepository.existsByUsername(authRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        if (userRepository.existsByEmail(authRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(authRequest.getUsername());
        user.setEmail(authRequest.getEmail());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        user.setGithubLink(authRequest.getGithubLink() != null ? authRequest.getGithubLink() : "");
        user.setSkills(authRequest.getSkills() != null ? authRequest.getSkills() : new HashSet<>());
        user.setBio(authRequest.getBio() != null ? authRequest.getBio() : "");
        
        logger.debug("Attempting to save user: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        logger.info("Successfully saved user with ID: {}", savedUser.getId());
        return savedUser;
    }

    public User createUser(User user) {
        logger.info("Creating user with username: {}", user.getUsername());
        
        logger.debug("Encoding password for user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        logger.debug("Attempting to save user: {}", user.getUsername());
        User savedUser = userRepository.save(user);
        
        logger.info("Successfully saved user with ID: {}", savedUser.getId());
        return savedUser;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> findAllActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    public List<User> findTopUsersByReputation(Integer minPoints) {
        return userRepository.findTopUsersByReputation(minPoints);
    }

    public List<User> findBySkills(List<String> skills) {
        return userRepository.findBySkills(skills);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void addReputationPoints(Long userId, Integer points) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.addReputationPoints(points);
            userRepository.save(user);
        }
    }

    public Long countVerifiedUsers() {
        return userRepository.countVerifiedUsers();
    }

    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }
} 