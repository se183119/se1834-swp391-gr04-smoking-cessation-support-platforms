package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.PlanMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanMilestoneRepository extends JpaRepository<PlanMilestone,Long> {
    List<PlanMilestone> findByQuitPlanIdOrderByStepIndex(Long planId);
    List<PlanMilestone> findByQuitPlanId(Long quitPlanId);
}