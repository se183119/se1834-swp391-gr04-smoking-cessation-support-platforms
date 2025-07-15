package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.QuitPlan;
import com.smokingcessation.platform.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress,Long> {
    Optional<UserProgress> findByQuitPlanIdAndLogDate(Long planId, LocalDate logDate);
    List<UserProgress> findByQuitPlanIdAndLogDateBetween(Long planId, LocalDate from, LocalDate to);
    List<UserProgress> findByQuitPlanId(Long quitPlanId);
    List<UserProgress> findByQuitPlan(QuitPlan quitPlan);
    List<UserProgress> findAllByQuitPlanId(Long quitPlanId);

}