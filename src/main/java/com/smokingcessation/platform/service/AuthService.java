package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final UserService userService;
    private final PasswordService passwordService;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserService userService, PasswordService passwordService, JwtService jwtService) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.jwtService = jwtService;
    }

    // ========== Authentication Operations ==========

    /**
     * Authenticate user with username/email and password
     */
    public AuthenticationResult authenticate(String usernameOrEmail, String password) {
        // Find user by username or email
        Optional<User> userOpt = userService.findByUsernameOrEmail(usernameOrEmail);

        if (userOpt.isEmpty()) {
            // Record failed attempt even for non-existent users (security)
            userService.recordFailedLoginAttempt(usernameOrEmail);
            return AuthenticationResult.failure("Invalid credentials");
        }

        User user = userOpt.get();

        // Check if account is active
        if (!user.getIsActive()) {
            return AuthenticationResult.failure("Account is deactivated");
        }

        // Check if account is locked
        if (userService.isAccountLocked(user.getId())) {
            return AuthenticationResult.failure("Account is temporarily locked due to multiple failed login attempts");
        }

        // Verify password
        if (!passwordService.verifyPassword(password, user.getPasswordHash())) {
            userService.recordFailedLoginAttempt(usernameOrEmail);
            return AuthenticationResult.failure("Invalid credentials");
        }

        // Successful authentication
        userService.recordSuccessfulLogin(user.getId());

        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername(), user.getId(), user.getRole().name());

        return AuthenticationResult.success(user, token);
    }

    /**
     * Register new user
     */
    public AuthenticationResult register(String username, String email, String password,
                                         String firstName, String lastName) {
        try {
            // Create new user
            User user = userService.registerMember(username, email, password, firstName, lastName);

            // Generate JWT token for immediate login
            String token = jwtService.generateToken(user.getUsername(), user.getId(), user.getRole().name());

            return AuthenticationResult.success(user, token);

        } catch (IllegalArgumentException e) {
            return AuthenticationResult.failure(e.getMessage());
        }
    }

    /**
     * Refresh JWT token
     */
    public AuthenticationResult refreshToken(String token) {
        try {
            // Validate current token
            if (!jwtService.validateToken(token)) {
                return AuthenticationResult.failure("Invalid or expired token");
            }

            // Extract user information
            String username = jwtService.extractUsername(token);
            Long userId = jwtService.extractUserId(token);
            String role = jwtService.extractUserRole(token);

            // Verify user still exists and is active
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
                return AuthenticationResult.failure("User no longer exists or is inactive");
            }

            // Generate new token
            String newToken = jwtService.generateToken(username, userId, role);

            return AuthenticationResult.success(userOpt.get(), newToken);

        } catch (Exception e) {
            return AuthenticationResult.failure("Token refresh failed");
        }
    }

    /**
     * Validate token and get user information
     */
    @Transactional(readOnly = true)
    public Optional<User> validateTokenAndGetUser(String token) {
        try {
            if (!jwtService.validateToken(token)) {
                return Optional.empty();
            }

            String username = jwtService.extractUsername(token);
            return userService.findByUsername(username);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Change user password
     */
    public AuthenticationResult changePassword(Long userId, String currentPassword, String newPassword) {
        try {
            userService.updatePassword(userId, currentPassword, newPassword);
            return AuthenticationResult.success("Password changed successfully");

        } catch (IllegalArgumentException e) {
            return AuthenticationResult.failure(e.getMessage());
        }
    }

    /**
     * Update user profile
     */
    public AuthenticationResult updateProfile(Long userId, String firstName, String lastName,
                                              String phoneNumber, String bio) {
        try {
            User updatedUser = userService.updateUserProfile(userId, firstName, lastName, phoneNumber, bio);
            return AuthenticationResult.success(updatedUser, null);

        } catch (IllegalArgumentException e) {
            return AuthenticationResult.failure(e.getMessage());
        }
    }

    // ========== Utility Methods ==========

    /**
     * Check if username is available
     */
    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return userService.findByUsername(username).isEmpty();
    }

    /**
     * Check if email is available
     */
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return userService.findByEmail(email).isEmpty();
    }

    // ========== Authentication Result Class ==========

    /**
     * Authentication result wrapper class
     */
    public static class AuthenticationResult {
        private final boolean success;
        private final String message;
        private final User user;
        private final String token;

        private AuthenticationResult(boolean success, String message, User user, String token) {
            this.success = success;
            this.message = message;
            this.user = user;
            this.token = token;
        }

        public static AuthenticationResult success(User user, String token) {
            return new AuthenticationResult(true, "Authentication successful", user, token);
        }

        public static AuthenticationResult success(String message) {
            return new AuthenticationResult(true, message, null, null);
        }

        public static AuthenticationResult failure(String message) {
            return new AuthenticationResult(false, message, null, null);
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
        public String getToken() { return token; }
    }
}