package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.UserAchievement;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    List<UserAchievement> findByUser(User user);

    List<UserAchievement> findByUserId(Long userId);

    List<UserAchievement> findByUserIdAndIsSharedTrue(Long userId);

    boolean existsByUserAndAchievement(User user, Achievement achievement);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId ORDER BY ua.earnedAt DESC")
    List<UserAchievement> findByUserIdOrderByEarnedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.isShared = true ORDER BY ua.sharedAt DESC")
    List<UserAchievement> findSharedAchievementsOrderBySharedAtDesc();
}
