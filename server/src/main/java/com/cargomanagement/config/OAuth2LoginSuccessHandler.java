package com.cargomanagement.config;

import com.cargomanagement.models.User;
import com.cargomanagement.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final String frontendUrl;
    private final Set<String> allowedRedirectOrigins;

    public OAuth2LoginSuccessHandler(
            AuthService authService,
            @Value("${app.frontend.url:http://localhost:5173}") String frontendUrl,
            @Value("${app.oauth.allowed-redirect-origins:http://localhost:5173,http://localhost:5174}") String allowedRedirectOrigins) {
        this.authService = authService;
        this.frontendUrl = frontendUrl;
        this.allowedRedirectOrigins = Arrays.stream(allowedRedirectOrigins.split(","))
            .map(String::trim)
            .filter(origin -> !origin.isBlank())
            .collect(Collectors.toSet());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        // Extract user information from OAuth2User
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        
        if (email != null) {
            // Check if user exists, if not create new user
            Optional<User> existingUser = authService.findByEmail(email);
            User user;
            
            if (existingUser.isEmpty()) {
                // Create new user for OAuth login
                user = new User();
                user.setEmail(email);
                user.setName(name != null ? name : email);
                user.setPicture(picture);
                user.setProvider("GOOGLE");
                user.setRole("PENDING");      // New OAuth users are PENDING
                user.setIsActive(false);      // Inactive until admin approves
                user = authService.saveUser(user);
            } else {
                user = existingUser.get();
                // Update picture if it changed
                if (picture != null && !picture.equals(user.getPicture())) {
                    user.setPicture(picture);
                    user = authService.saveUser(user);
                }
            }
            
            // Store user info in session
            HttpSession session = request.getSession(true);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
        }
        
        String redirectUrl = determineRedirectUrl(request);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl + "/oauth-callback");
    }
    
    private String determineRedirectUrl(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");

        if (origin != null && allowedRedirectOrigins.contains(origin)) {
            return origin;
        }

        String refererOrigin = extractOrigin(referer);
        if (refererOrigin != null && allowedRedirectOrigins.contains(refererOrigin)) {
            return refererOrigin;
        }

        return frontendUrl;
    }

    private String extractOrigin(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }

        try {
            URI uri = URI.create(url);
            if (uri.getScheme() != null && uri.getHost() != null) {
                StringBuilder origin = new StringBuilder(uri.getScheme()).append("://").append(uri.getHost());
                if (uri.getPort() != -1) {
                    origin.append(':').append(uri.getPort());
                }
                return origin.toString();
            }
        } catch (IllegalArgumentException ignored) {
        }

        return null;
    }
}
