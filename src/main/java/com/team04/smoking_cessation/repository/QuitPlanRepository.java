package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.QuitPlan;
import com.team04.smoking_cessation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuitPlanRepository extends JpaRepository<QuitPlan, Long> {

    List<QuitPlan> findByUserOrderByCreatedAtDesc(User user);

    Optional<QuitPlan> findByUserAndStatus(User user, QuitPlan.PlanStatus status);

    @Query("SELECT qp FROM QuitPlan qp WHERE qp.user = :user AND qp.status = 'ACTIVE'")
    Optional<QuitPlan> findActiveQuitPlanByUser(@Param("user") User user);

    List<QuitPlan> findByIsAiGeneratedTrue();

    @Query("SELECT COUNT(qp) FROM QuitPlan qp WHERE qp.status = :status")
    Long countByStatus(@Param("status") QuitPlan.PlanStatus status);
}
