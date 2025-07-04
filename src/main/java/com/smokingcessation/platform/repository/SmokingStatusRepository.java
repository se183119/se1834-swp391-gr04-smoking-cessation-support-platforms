package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.SmokingStatus;
import com.smokingcessation.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SmokingStatusRepository extends JpaRepository<SmokingStatus, Long> {

    Optional<SmokingStatus> findByUser(User user);

    Optional<SmokingStatus> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
