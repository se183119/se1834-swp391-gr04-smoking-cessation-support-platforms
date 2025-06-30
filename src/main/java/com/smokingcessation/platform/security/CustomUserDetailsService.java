package com.smokingcessation.platform.security;

import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Find user by username or email
        Optional<User> userOpt = userService.findByUsernameOrEmail(usernameOrEmail);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }

        User user = userOpt.get();

        // Check if user is active
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + usernameOrEmail);
        }

        // Check if user is deleted
        if (user.getIsDeleted()) {
            throw new UsernameNotFoundException("User account is deleted: " + usernameOrEmail);
        }

        // Check if account is locked
        if (isAccountLocked(user)) {
            throw new UsernameNotFoundException("User account is temporarily locked: " + usernameOrEmail);
        }

        // Create Spring Security UserDetails
        return createUserDetails(user);
    }

    /**
     * Create Spring Security UserDetails from User entity
     */
    private UserDetails createUserDetails(User user) {
        List<GrantedAuthority> authorities = getAuthorities(user);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(isAccountLocked(user))
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }

    /**
     * Get user authorities based on role
     */
    private List<GrantedAuthority> getAuthorities(User user) {
        // Create role-based authority
        String roleName = "ROLE_" + user.getRole().name();
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    /**
     * Check if account is locked
     */
    private boolean isAccountLocked(User user) {
        if (user.getAccountLockedUntil() == null) {
            return false;
        }
        return user.getAccountLockedUntil().isAfter(LocalDateTime.now());
    }

    /**
     * Load user by user ID (for token validation)
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        Optional<User> userOpt = userService.findById(userId);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with ID: " + userId);
        }

        User user = userOpt.get();

        // Same validation as loadUserByUsername
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + userId);
        }

        if (user.getIsDeleted()) {
            throw new UsernameNotFoundException("User account is deleted: " + userId);
        }

        if (isAccountLocked(user)) {
            throw new UsernameNotFoundException("User account is temporarily locked: " + userId);
        }

        return createUserDetails(user);
    }
}
