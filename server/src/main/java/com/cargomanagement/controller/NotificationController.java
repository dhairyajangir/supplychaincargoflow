package com.cargomanagement.controller;

import com.cargomanagement.dto.NotificationRequest;
import com.cargomanagement.service.AuthService;
import com.cargomanagement.service.NotificationService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    public NotificationController(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @PostMapping("/email")
    public ResponseEntity<Map<String, Object>> sendEmailNotification(
            @Valid @RequestBody NotificationRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        String requesterEmail = resolveCurrentUserEmail(authentication, httpRequest);
        if (requesterEmail == null || !authService.isActiveAdmin(requesterEmail)) {
            Map<String, Object> forbiddenResponse = new HashMap<>();
            forbiddenResponse.put("success", false);
            forbiddenResponse.put("message", "Admin access required");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(forbiddenResponse);
        }

        boolean sent = notificationService.sendCustomEmail(request.getRecipients(), request.getSubject(), request.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("success", sent);
        response.put("subject", request.getSubject());
        response.put("message", sent ? "Notification email dispatched." : "Notification skipped - no recipients configured.");
        HttpStatus status = sent ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }

    private String resolveCurrentUserEmail(Authentication authentication, HttpServletRequest request) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof OAuth2User oauth2User) {
                return oauth2User.getAttribute("email");
            }
            if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
                return userDetails.getUsername();
            }
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        Object sessionEmail = session.getAttribute("userEmail");
        return sessionEmail instanceof String ? (String) sessionEmail : null;
    }
}
