package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

    List<SubscriptionPlan> findByIsActiveTrue();

    Optional<SubscriptionPlan> findByNameIgnoreCase(String name);

    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.isActive = true ORDER BY sp.monthlyPrice ASC")
    List<SubscriptionPlan> findActiveOrderByPrice();
}
