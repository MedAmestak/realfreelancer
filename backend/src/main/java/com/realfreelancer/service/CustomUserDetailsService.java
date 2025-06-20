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
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        
        // Always treat the username as email for authentication
        Optional<User> userOptional = userRepository.findByEmail(username);

        if (userOptional.isPresent()) {
            user = userOptional.get();
            return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build();
        } else {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
    }
} 