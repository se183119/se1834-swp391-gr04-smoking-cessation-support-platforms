package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.enums.UserRole;
import com.smokingcessation.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;
    private static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;

    @Autowired
    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    // ========== User Creation & Registration ==========

    /**
     * Create new user with validation
     */
    public User createUser(String username, String email, String password,
                           String firstName, String lastName, UserRole role) {

        // Validate input
        validateUserInput(username, email, password, firstName, lastName);

        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // Create and configure user
        User user = new User();
        user.setUsername(username.trim().toLowerCase());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(passwordService.encodePassword(password));
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setRole(role != null ? role : UserRole.MEMBER);
        user.setIsActive(true);
        user.setEmailVerified(false);
        user.setFailedLoginAttempts(0);

        return userRepository.save(user);
    }

    /**
     * Register new member user
     */
    public User registerMember(String username, String email, String password,
                               String firstName, String lastName) {
        return createUser(username, email, password, firstName, lastName, UserRole.MEMBER);
    }

    // ========== User Retrieval ==========

    /**
     * Find user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.getIsDeleted());
    }

    /**
     * Find user by username
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim());
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email.trim());
    }

    /**
     * Find user by username or email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail == null || usernameOrEmail.trim().isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsernameOrEmail(usernameOrEmail.trim());
    }

    /**
     * Get all active users with pagination
     */
    @Transactional(readOnly = true)
    public Page<User> findAllActiveUsers(Pageable pageable) {
        return userRepository.findAllActiveUsers(pageable);
    }

    /**
     * Get users by role with pagination
     */
    @Transactional(readOnly = true)
    public Page<User> findUsersByRole(UserRole role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    // ========== User Update Operations ==========

    /**
     * Update user profile
     */
    public User updateUserProfile(Long userId, String firstName, String lastName,
                                  String phoneNumber, String bio) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (firstName != null && !firstName.trim().isEmpty()) {
            user.setFirstName(firstName.trim());
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            user.setLastName(lastName.trim());
        }

        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber.trim().isEmpty() ? null : phoneNumber.trim());
        }

        if (bio != null) {
            user.setBio(bio.trim().isEmpty() ? null : bio.trim());
        }

        return userRepository.save(user);
    }

    /**
     * Update user password
     */
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Verify current password
        if (!passwordService.verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password
        if (!passwordService.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("Invalid password format. " +
                    passwordService.getPasswordRequirements());
        }

        // Update password
        user.setPasswordHash(passwordService.encodePassword(newPassword));
        userRepository.save(user);
    }

    /**
     * Update user email
     */
    public void updateEmail(Long userId, String newEmail) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        // Validate email format
        if (!isValidEmail(newEmail)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Check if email already exists
        if (userRepository.existsByEmailAndIdNot(newEmail.trim().toLowerCase(), userId)) {
            throw new IllegalArgumentException("Email already exists: " + newEmail);
        }

        user.setEmail(newEmail.trim().toLowerCase());
        user.setEmailVerified(false); // Reset email verification
        userRepository.save(user);
    }

    // ========== Account Security Operations ==========

    /**
     * Handle successful login
     */
    public void recordSuccessfulLogin(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setLastLoginAt(LocalDateTime.now());
        user.setFailedLoginAttempts(0);
        user.setAccountLockedUntil(null);
        userRepository.save(user);
    }

    /**
     * Handle failed login attempt
     */
    public void recordFailedLoginAttempt(String usernameOrEmail) {
        Optional<User> userOpt = findByUsernameOrEmail(usernameOrEmail);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            // Lock account if max attempts reached
            if (attempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
                user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(ACCOUNT_LOCK_DURATION_MINUTES));
            }

            userRepository.save(user);
        }
    }

    /**
     * Check if account is locked
     */
    @Transactional(readOnly = true)
    public boolean isAccountLocked(Long userId) {
        User user = findById(userId).orElse(null);
        if (user == null) return false;

        return user.getAccountLockedUntil() != null &&
                user.getAccountLockedUntil().isAfter(LocalDateTime.now());
    }

    /**
     * Unlock user account
     */
    public void unlockAccount(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);
        userRepository.save(user);
    }

    // ========== Account Status Operations ==========

    /**
     * Activate user account
     */
    public void activateUser(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setIsActive(true);
        userRepository.save(user);
    }

    /**
     * Deactivate user account
     */
    public void deactivateUser(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Verify user email
     */
    public void verifyEmail(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setEmailVerified(true);
        userRepository.save(user);
    }

    /**
     * Soft delete user
     */
    public void deleteUser(Long userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
    }

    // ========== Validation Methods ==========

    /**
     * Validate user input data
     */
    private void validateUserInput(String username, String email, String password,
                                   String firstName, String lastName) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (username.trim().length() < 3 || username.trim().length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (!passwordService.isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid password format. " +
                    passwordService.getPasswordRequirements());
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.trim().matches(emailRegex);
    }

    // ========== Statistics Methods ==========

    /**
     * Count users by role
     */
    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    /**
     * Count active users
     */
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }

    /**
     * Count users registered today
     */
    @Transactional(readOnly = true)
    public long countUsersRegisteredToday() {
        return userRepository.countUsersRegisteredToday();
    }
}
