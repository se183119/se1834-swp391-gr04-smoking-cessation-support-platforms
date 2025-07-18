package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.membershipPackage IS NOT NULL AND u.membershipExpiry > CURRENT_TIMESTAMP")
    List<User> findActiveMembers();

    List<User> findByStatus(User.UserStatus status);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'COACH'")
    List<User> findAllCoaches();
}
