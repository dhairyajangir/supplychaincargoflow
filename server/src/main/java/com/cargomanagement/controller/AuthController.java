package com.cargomanagement.controller;

import com.cargomanagement.models.User;
import com.cargomanagement.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            String password = request.get("password");
            String name = request.get("name");

            if (email == null || password == null || name == null) {
                response.put("success", false);
                response.put("message", "Email, password, and name are required");
                return ResponseEntity.badRequest().body(response);
            }

            User user = authService.registerUser(email, password, name);
            
            response.put("success", true);
            response.put("message", "Registration successful. Your account is pending admin approval.");
            response.put("isPending", true);
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            String password = request.get("password");

            if (email == null || password == null) {
                response.put("success", false);
                response.put("message", "Email and password are required");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if user exists first
            Optional<User> checkUser = authService.findByEmail(email);
            if (checkUser.isPresent() && !checkUser.get().getIsActive()) {
                // User exists but is not active (pending approval)
                response.put("success", false);
                response.put("message", "Account pending approval");
                response.put("isPending", true);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            Optional<User> userOpt = authService.authenticateUser(email, password);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Create Spring Security Authentication
                org.springframework.security.core.userdetails.User principal = 
                    new org.springframework.security.core.userdetails.User(
                        user.getEmail(), 
                        user.getPassword(), 
                        java.util.Collections.emptyList()
                    );
                
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken authentication = 
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        principal, 
                        null, 
                        java.util.Collections.emptyList()
                    );
                
                // Set authentication in SecurityContext
                org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
                
                // Create session and store SecurityContext
                HttpSession session = httpRequest.getSession(true);
                session.setAttribute("SPRING_SECURITY_CONTEXT", 
                    org.springframework.security.core.context.SecurityContextHolder.getContext());
                session.setAttribute("userId", user.getId());
                session.setAttribute("userEmail", user.getEmail());
                
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("user", createUserResponse(user));
                
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            Authentication authentication,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                
                // OAuth2 User
                if (principal instanceof OAuth2User) {
                    OAuth2User oAuth2User = (OAuth2User) principal;
                    String email = oAuth2User.getAttribute("email");
                    
                    Optional<User> userOpt = authService.findByEmail(email);
                    if (userOpt.isPresent()) {
                        response.put("success", true);
                        response.put("user", createUserResponse(userOpt.get()));
                        return ResponseEntity.ok(response);
                    }
                }
            }
            
            // Check session for local auth
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                String email = (String) session.getAttribute("userEmail");
                
                Optional<User> userOpt = authService.findByEmail(email);
                if (userOpt.isPresent()) {
                    response.put("success", true);
                    response.put("user", createUserResponse(userOpt.get()));
                    return ResponseEntity.ok(response);
                }
            }
            
            response.put("success", false);
            response.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/csrf-token")
    public ResponseEntity<Map<String, String>> csrfToken(CsrfToken csrfToken) {
        Map<String, String> response = new HashMap<>();
        response.put("token", csrfToken.getToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            response.put("success", true);
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("name", user.getName());
        userMap.put("picture", user.getPicture());
        userMap.put("provider", user.getProvider());
        userMap.put("role", user.getRole());
        return userMap;
    }
}
