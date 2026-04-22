package com.cargomanagement.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes.
 * Usage: provide the raw password as the first argument.
 */
public class PasswordHashGenerator {
    public static void main(String[] args) {
        if (args.length == 0 || args[0] == null || args[0].isBlank()) {
            System.err.println("Usage: java PasswordHashGenerator <raw-password>");
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = args[0];
        String hashedPassword = encoder.encode(rawPassword);

        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("'" + hashedPassword + "'");
    }
}
