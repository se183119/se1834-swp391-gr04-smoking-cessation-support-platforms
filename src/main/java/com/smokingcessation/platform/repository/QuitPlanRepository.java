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
    List<QuitPlan> findByUserId(Long userId);
    Optional<QuitPlan> findTopByUserIdOrderByStartDateDesc(Long userId);
    QuitPlan findByUserAndIsDoneFalse(User user);
}
