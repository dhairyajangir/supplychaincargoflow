package com.cargomanagement.controller;

import com.cargomanagement.models.User;
import com.cargomanagement.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class UserManagementController {

    private final AuthService authService;

    public UserManagementController(AuthService authService) {
        this.authService = authService;
    }

    // Check if current user is admin
    private boolean isAdmin(Authentication authentication, HttpServletRequest request) {
        // Check OAuth2 authentication
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof OAuth2User) {
                OAuth2User oAuth2User = (OAuth2User) principal;
                String email = oAuth2User.getAttribute("email");
                return authService.isActiveAdmin(email);
            }

            if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
                return authService.isActiveAdmin(userDetails.getUsername());
            }
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            if (session.getAttribute("userEmail") != null) {
                String email = (String) session.getAttribute("userEmail");
                return authService.isActiveAdmin(email);
            }
        }

        return false;
    }

    // Get all pending users (admin only)
    @GetMapping("/pending-users")
    public ResponseEntity<?> getPendingUsers(
            Authentication authentication,
            HttpServletRequest request) {

        try {
            if (!isAdmin(authentication, request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Admin access required"));
            }

            List<User> pendingUsers = authService.getPendingUsers();

            pendingUsers.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(pendingUsers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "message", "Failed to fetch pending users"
                ));
        }
    }

    // Get all users (admin only)
    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers(
            Authentication authentication,
            HttpServletRequest request) {
        
        if (!isAdmin(authentication, request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Admin access required"));
        }

        List<User> allUsers = authService.getAllUsers();
        
        // Remove passwords from response
        allUsers.forEach(user -> user.setPassword(null));
        
        return ResponseEntity.ok(allUsers);
    }

    // Approve user (admin only)
    @PostMapping("/approve-user/{userId}")
    public ResponseEntity<?> approveUser(
            @PathVariable Long userId,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        
        if (!isAdmin(authentication, httpRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Admin access required"));
        }

        try {
            User user = authService.approveUser(userId);
            user.setPassword(null); // Remove password from response
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User approved successfully",
                "user", user
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    // Reject user (admin only)
    @DeleteMapping("/reject-user/{userId}")
    public ResponseEntity<?> rejectUser(
            @PathVariable Long userId,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        
        if (!isAdmin(authentication, httpRequest)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Admin access required"));
        }

        try {
            authService.rejectUser(userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User rejected and deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
