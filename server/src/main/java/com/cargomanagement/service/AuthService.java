package com.cargomanagement.service;

import com.cargomanagement.models.User;
import com.cargomanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String password, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setProvider("local");
        user.setRole("PENDING");  // New users are PENDING
        user.setIsActive(false);   // Inactive until admin approves

        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Check if user is active and password matches
            if (user.getProvider().equals("local") && 
                passwordEncoder.matches(password, user.getPassword()) &&
                user.getIsActive()) {
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isActiveAdmin(String email) {
        return userRepository.findByEmail(email)
            .map(user -> "ADMIN".equals(user.getRole()) && Boolean.TRUE.equals(user.getIsActive()))
            .orElse(false);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Admin methods to manage users
    public List<User> getPendingUsers() {
        return userRepository.findByRole("PENDING");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole("OPERATOR");
        user.setIsActive(true);
        
        return userRepository.save(user);
    }

    public void rejectUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Delete rejected users
        userRepository.delete(user);
    }
}

