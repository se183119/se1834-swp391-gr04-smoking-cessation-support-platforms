package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.QuitPlan;
import com.smokingcessation.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuitPlanRepository extends JpaRepository<QuitPlan, Long> {

    List<QuitPlan> findByUser(User user);

    List<QuitPlan> findByUserId(Long userId);

    List<QuitPlan> findByStatus(QuitPlan.PlanStatus status);

    @Query("SELECT qp FROM QuitPlan qp WHERE qp.user.id = :userId AND qp.status = :status")
    Optional<QuitPlan> findActiveQuitPlanByUserId(@Param("userId") Long userId, @Param("status") QuitPlan.PlanStatus status);

    @Query("SELECT qp FROM QuitPlan qp WHERE qp.user.id = :userId ORDER BY qp.createdAt DESC")
    List<QuitPlan> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
