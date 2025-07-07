package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserAndStatus(User user, UserSubscription.SubscriptionStatus status);

    List<UserSubscription> findByUserOrderByCreatedAtDesc(User user);

    List<UserSubscription> findByStatus(UserSubscription.SubscriptionStatus status);

    @Query("SELECT us FROM UserSubscription us WHERE us.nextBillingDate <= ?1 AND us.status = 'ACTIVE'")
    List<UserSubscription> findSubscriptionsDueForBilling(LocalDateTime date);

    @Query("SELECT us FROM UserSubscription us WHERE us.endDate <= ?1 AND us.status = 'ACTIVE'")
    List<UserSubscription> findExpiringSubscriptions(LocalDateTime date);

    Optional<UserSubscription> findByPayosTransactionId(String payosTransactionId);
    Optional<UserSubscription> findByOrderCode(String orderCode);

    @Query("SELECT COUNT(us) FROM UserSubscription us WHERE us.plan.name = ?1 AND us.status = 'ACTIVE'")
    Long countActiveSubscriptionsByPlan(String planName);

    @Query("SELECT us FROM UserSubscription us WHERE us.user = ?1 AND us.status = 'ACTIVE' ORDER BY us.createdAt DESC")
    List<UserSubscription> findActiveSubscriptionsByUser(User user);
} 