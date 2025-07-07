package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {

    List<Badge> findByIsActiveTrueOrderByRequiredValueAsc();

    Optional<Badge> findByNameIgnoreCase(String name);

    boolean existsByName(String name);

    List<Badge> findByTypeAndIsActiveTrue(Badge.BadgeType type);

    List<Badge> findByCategoryAndIsActiveTrue(Badge.BadgeCategory category);
}
