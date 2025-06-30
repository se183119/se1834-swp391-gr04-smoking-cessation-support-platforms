package com.smokingcessation.platform.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.regex.Pattern;

@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;

    // Password validation patterns
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    // Password strength patterns
    private static final Pattern WEAK_PASSWORD_PATTERN = Pattern.compile("^.{1,7}$");
    private static final Pattern MEDIUM_PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d).{8,}$");
    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$"
    );

    public PasswordService() {
        this.passwordEncoder = new BCryptPasswordEncoder(12); // Strength 12
        this.secureRandom = new SecureRandom();
    }

    /**
     * Encode password using BCrypt
     */
    public String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verify password against encoded password
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Validate password strength
     */
    public boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Get password strength level
     */
    public PasswordStrength getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return PasswordStrength.INVALID;
        }

        if (WEAK_PASSWORD_PATTERN.matcher(password).matches()) {
            return PasswordStrength.WEAK;
        }

        if (STRONG_PASSWORD_PATTERN.matcher(password).matches()) {
            return PasswordStrength.STRONG;
        }

        if (MEDIUM_PASSWORD_PATTERN.matcher(password).matches()) {
            return PasswordStrength.MEDIUM;
        }

        return PasswordStrength.WEAK;
    }

    /**
     * Generate random password
     */
    public String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password length must be at least 8 characters");
        }

        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@$!%*?&";
        String allChars = uppercase + lowercase + digits + special;

        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(uppercase.charAt(secureRandom.nextInt(uppercase.length())));
        password.append(lowercase.charAt(secureRandom.nextInt(lowercase.length())));
        password.append(digits.charAt(secureRandom.nextInt(digits.length())));
        password.append(special.charAt(secureRandom.nextInt(special.length())));

        // Fill remaining length with random characters
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(secureRandom.nextInt(allChars.length())));
        }

        // Shuffle the password
        return shuffleString(password.toString());
    }

    /**
     * Shuffle string characters randomly
     */
    private String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int randomIndex = secureRandom.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }

    /**
     * Get password validation requirements
     */
    public String getPasswordRequirements() {
        return "Password must be at least 8 characters long and contain:\n" +
                "- At least one lowercase letter (a-z)\n" +
                "- At least one uppercase letter (A-Z)\n" +
                "- At least one digit (0-9)\n" +
                "- At least one special character (@$!%*?&)";
    }

    /**
     * Check if password is commonly used
     */
    public boolean isCommonPassword(String password) {
        if (password == null) return false;

        // List of common passwords to avoid
        String[] commonPasswords = {
                "password", "12345678", "qwerty", "abc123", "password123",
                "admin", "letmein", "welcome", "monkey", "1234567890"
        };

        String lowercasePassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowercasePassword.equals(common)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Password strength enumeration
     */
    public enum PasswordStrength {
        INVALID("Invalid"),
        WEAK("Weak"),
        MEDIUM("Medium"),
        STRONG("Strong");

        private final String description;

        PasswordStrength(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
