package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.Achievement;
import com.team04.smoking_cessation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByUserOrderByEarnedAtDesc(User user);

    @Query("SELECT a.user.id, a.user.fullName, COUNT(a), " +
           "(SELECT COUNT(dl) FROM DailyLog dl WHERE dl.user = a.user AND dl.isSmokeFree = true) " +
           "FROM Achievement a " +
           "GROUP BY a.user.id, a.user.fullName " +
           "ORDER BY COUNT(a) DESC " +
           "LIMIT 10")
    List<Object[]> getTopUsersByAchievementCount();

    Long countByUser(User user);

    boolean existsByUserAndBadge_Id(User user, Long badgeId);
}
