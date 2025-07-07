package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.UserRole;
import com.team04.smoking_cessation.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatus(String email, AccountStatus status);

    boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByStatus(AccountStatus status);

    Optional<User> findByEmailVerificationToken(String token);

    @Query("SELECT u FROM User u WHERE u.emailVerified = true AND u.status = :status")
    List<User> findVerifiedUsersByStatus(@Param("status") AccountStatus status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.status = :status")
    Long countByRoleAndStatus(@Param("role") UserRole role, @Param("status") AccountStatus status);

    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:searchTerm% OR u.email LIKE %:searchTerm%")
    List<User> searchUsersList(@Param("searchTerm") String searchTerm);

    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:searchTerm% OR u.email LIKE %:searchTerm%")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    Long countByStatus(AccountStatus status);
}
