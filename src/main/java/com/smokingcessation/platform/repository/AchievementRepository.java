package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByType(Achievement.AchievementType type);

    List<Achievement> findByLevel(Achievement.AchievementLevel level);

    List<Achievement> findByIsShareableTrue();

    List<Achievement> findByTypeOrderByTargetValueAsc(Achievement.AchievementType type);
}
