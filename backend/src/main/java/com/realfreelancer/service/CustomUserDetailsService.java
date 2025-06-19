package com.realfreelancer.service;

import com.realfreelancer.model.User;
import com.realfreelancer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        
        // Always treat the username as email for authentication
        user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        System.out.println("Found user by email: " + user.getUsername()); // Debug log
        System.out.println("Password hash: " + user.getPassword()); // Debug log

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // Use email as the principal
                user.getPassword(),
                user.getIsActive(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 