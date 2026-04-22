package com.cargomanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final String frontendUrl;

    public SecurityConfig(
            @Lazy OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            @Value("${app.frontend.url:http://localhost:5173}") String frontendUrl) {
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.frontendUrl = frontendUrl;
    }

    // This bean allows AuthService to inject PasswordEncoder successfully
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configure security with OAuth2 login
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/error", "/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/user", "/api/auth/csrf-token").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/signup").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2LoginSuccessHandler)
                .failureUrl(frontendUrl + "/login?error=true")
            );
        return http.build();
    }
}
