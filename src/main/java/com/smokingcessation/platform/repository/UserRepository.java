package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ========== Authentication Queries ==========

    /**
     * Find user by username (case-insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username) AND u.isDeleted = false")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * Find user by email (case-insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.isDeleted = false")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Find user by username or email
     */
    @Query("SELECT u FROM User u WHERE (LOWER(u.username) = LOWER(:usernameOrEmail) OR LOWER(u.email) = LOWER(:usernameOrEmail)) AND u.isDeleted = false")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    // ========== Validation Queries ==========

    /**
     * Check if username exists (case-insensitive)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.username) = LOWER(:username) AND u.isDeleted = false")
    boolean existsByUsername(@Param("username") String username);

    /**
     * Check if email exists (case-insensitive)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.isDeleted = false")
    boolean existsByEmail(@Param("email") String email);

    /**
     * Check if username exists excluding specific user ID
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.username) = LOWER(:username) AND u.id != :userId AND u.isDeleted = false")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("userId") Long userId);

    /**
     * Check if email exists excluding specific user ID
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email) AND u.id != :userId AND u.isDeleted = false")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("userId") Long userId);

    // ========== User Management Queries ==========

    /**
     * Find all active users
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.isDeleted = false")
    Page<User> findAllActiveUsers(Pageable pageable);

    /**
     * Find users by role
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isDeleted = false")
    Page<User> findByRole(@Param("role") UserRole role, Pageable pageable);

    /**
     * Find users created within date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND u.isDeleted = false")
    List<User> findUsersCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // ========== Security Queries ==========

    /**
     * Find users with failed login attempts
     */
    @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= :threshold AND u.isDeleted = false")
    List<User> findUsersWithFailedLoginAttempts(@Param("threshold") int threshold);

    /**
     * Find locked users
     */
    @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > :currentTime AND u.isDeleted = false")
    List<User> findLockedUsers(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find users with unverified emails
     */
    @Query("SELECT u FROM User u WHERE u.emailVerified = false AND u.isDeleted = false")
    List<User> findUsersWithUnverifiedEmails();

    // ========== Statistics Queries ==========

    /**
     * Count users by role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isDeleted = false")
    long countByRole(@Param("role") UserRole role);

    /**
     * Count active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true AND u.isDeleted = false")
    long countActiveUsers();

    /**
     * Count users registered today - FIXED QUERY
     */
    @Query("SELECT COUNT(u) FROM User u WHERE CAST(u.createdAt AS date) = CAST(CURRENT_DATE AS date) AND u.isDeleted = false")
    long countUsersRegisteredToday();
}
