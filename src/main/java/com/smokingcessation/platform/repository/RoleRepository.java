package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(Role.RoleName name);

    boolean existsByName(Role.RoleName name);
}
