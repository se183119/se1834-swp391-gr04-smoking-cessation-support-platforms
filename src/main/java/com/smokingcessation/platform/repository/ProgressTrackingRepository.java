package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.ProgressTracking;
import com.smokingcessation.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressTrackingRepository extends JpaRepository<ProgressTracking, Long> {

    List<ProgressTracking> findByUser(User user);

    List<ProgressTracking> findByUserId(Long userId);

    Optional<ProgressTracking> findByUserIdAndTrackingDate(Long userId, LocalDate trackingDate);

    @Query("SELECT pt FROM ProgressTracking pt WHERE pt.user.id = :userId ORDER BY pt.trackingDate DESC")
    List<ProgressTracking> findByUserIdOrderByTrackingDateDesc(@Param("userId") Long userId);
 
    @Query("SELECT pt FROM ProgressTracking pt WHERE pt.user.id = :userId AND pt.trackingDate BETWEEN :startDate AND :endDate")
    List<ProgressTracking> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);

    @Query("SELECT MAX(pt.currentStreak) FROM ProgressTracking pt WHERE pt.user.id = :userId")
    Optional<Integer> findMaxStreakByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(pt.moneySaved) FROM ProgressTracking pt WHERE pt.user.id = :userId")
    Optional<Double> getTotalMoneySavedByUserId(@Param("userId") Long userId);
}
