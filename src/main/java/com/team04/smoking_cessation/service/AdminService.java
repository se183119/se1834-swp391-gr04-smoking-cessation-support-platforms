package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.*;
import com.team04.smoking_cessation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        // User statistics
        dashboard.put("totalUsers", userRepository.count());
        dashboard.put("totalMembers", userRepository.countByRoleAndStatus(UserRole.MEMBER, AccountStatus.ACTIVE));
        dashboard.put("totalCoaches", userRepository.countByRoleAndStatus(UserRole.COACH, AccountStatus.ACTIVE));
        dashboard.put("newUsersThisMonth", userRepository.count()); // TODO: Add date filter

        // Platform statistics
        dashboard.put("totalPosts", forumPostRepository.count());
        dashboard.put("totalAchievements", achievementRepository.count());
        dashboard.put("activePlans", subscriptionPlanRepository.findByIsActiveTrue().size());

        // Recent activity
        List<User> recentUsers = userRepository.findAll().stream()
            .sorted((u1, u2) -> u2.getCreatedAt().compareTo(u1.getCreatedAt()))
            .limit(5)
            .toList();
        dashboard.put("recentUsers", recentUsers);

        return dashboard;
    }

    public Page<User> getAllUsers(Pageable pageable, String search) {
        if (search != null && !search.isEmpty()) {
            return userRepository.searchUsers(search, pageable);
        }
        return userRepository.findAll(pageable);
    }

    public void approveUserAsCoach(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(UserRole.COACH);
        userRepository.save(user);
    }

    public void suspendUser(Long userId, String reason) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(AccountStatus.SUSPENDED);
        userRepository.save(user);

        // TODO: Log suspension reason
    }

    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
    }

    public List<SubscriptionPlan> getAllSubscriptionPlans() {
        return subscriptionPlanRepository.findAll();
    }

    public SubscriptionPlan createSubscriptionPlan(Map<String, Object> planData) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName((String) planData.get("name"));
        plan.setDescription((String) planData.get("description"));
        plan.setMonthlyPrice(new BigDecimal(planData.get("monthlyPrice").toString()));
        plan.setYearlyPrice(new BigDecimal(planData.get("yearlyPrice").toString()));
        plan.setFeatures((String) planData.get("features"));

        return subscriptionPlanRepository.save(plan);
    }

    public SubscriptionPlan updateSubscriptionPlan(Long planId, Map<String, Object> planData) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Subscription plan not found"));

        if (planData.containsKey("name")) {
            plan.setName((String) planData.get("name"));
        }
        if (planData.containsKey("description")) {
            plan.setDescription((String) planData.get("description"));
        }
        if (planData.containsKey("monthlyPrice")) {
            plan.setMonthlyPrice(new BigDecimal(planData.get("monthlyPrice").toString()));
        }
        if (planData.containsKey("yearlyPrice")) {
            plan.setYearlyPrice(new BigDecimal(planData.get("yearlyPrice").toString()));
        }

        return subscriptionPlanRepository.save(plan);
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public Badge createBadge(Map<String, Object> badgeData) {
        Badge badge = new Badge();
        badge.setName((String) badgeData.get("name"));
        badge.setDescription((String) badgeData.get("description"));
        badge.setType(Badge.BadgeType.valueOf((String) badgeData.get("type")));
        badge.setCategory(Badge.BadgeCategory.valueOf((String) badgeData.get("category")));
        badge.setRequiredValue((Integer) badgeData.get("requiredValue"));
        badge.setIconUrl((String) badgeData.get("iconUrl"));
        badge.setColor((String) badgeData.get("color"));

        return badgeRepository.save(badge);
    }

    public Badge updateBadge(Long badgeId, Map<String, Object> badgeData) {
        Badge badge = badgeRepository.findById(badgeId)
            .orElseThrow(() -> new RuntimeException("Badge not found"));

        if (badgeData.containsKey("name")) {
            badge.setName((String) badgeData.get("name"));
        }
        if (badgeData.containsKey("description")) {
            badge.setDescription((String) badgeData.get("description"));
        }
        if (badgeData.containsKey("requiredValue")) {
            badge.setRequiredValue((Integer) badgeData.get("requiredValue"));
        }

        return badgeRepository.save(badge);
    }

    public void deleteBadge(Long badgeId) {
        Badge badge = badgeRepository.findById(badgeId)
            .orElseThrow(() -> new RuntimeException("Badge not found"));

        badge.setIsActive(false);
        badgeRepository.save(badge);
    }

    public List<Map<String, Object>> getReportedPosts() {
        // TODO: Implement post reporting system
        return List.of();
    }

    public void moderatePost(Long postId, String action, String reason) {
        ForumPost post = forumPostRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        switch (action.toUpperCase()) {
            case "HIDE":
                post.setStatus(ForumPost.PostStatus.HIDDEN);
                break;
            case "DELETE":
                post.setStatus(ForumPost.PostStatus.DELETED);
                break;
            case "APPROVE":
                post.setStatus(ForumPost.PostStatus.ACTIVE);
                break;
        }

        forumPostRepository.save(post);
    }

    public Map<String, Object> getUserAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalUsers", userRepository.count());
        analytics.put("activeUsers", userRepository.countByStatus(AccountStatus.ACTIVE));
        analytics.put("suspendedUsers", userRepository.countByStatus(AccountStatus.SUSPENDED));
        analytics.put("memberCount", userRepository.countByRoleAndStatus(UserRole.MEMBER, AccountStatus.ACTIVE));
        analytics.put("coachCount", userRepository.countByRoleAndStatus(UserRole.COACH, AccountStatus.ACTIVE));

        return analytics;
    }

    public Map<String, Object> getPlatformAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("totalPosts", forumPostRepository.count());
        analytics.put("totalAchievements", achievementRepository.count());
        analytics.put("totalDailyLogs", dailyLogRepository.count());

        // Success metrics
        Long usersWithProgress = dailyLogRepository.countUsersWithSmokeFreeDays();
        analytics.put("usersWithProgress", usersWithProgress);

        return analytics;
    }

    public Map<String, Object> getRevenueAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // TODO: Implement revenue calculations from subscriptions
        analytics.put("totalRevenue", 0);
        analytics.put("monthlyRevenue", 0);
        analytics.put("subscriptionBreakdown", Map.of(
            "FREE", 0,
            "PREMIUM", 0
        ));

        return analytics;
    }

    public Map<String, Object> getSystemConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put("platformName", "Smoking Cessation Platform");
        config.put("version", "1.0.0");
        config.put("maintenanceMode", false);
        config.put("registrationEnabled", true);
        config.put("emailVerificationRequired", true);

        return config;
    }

    public void updateSystemConfig(Map<String, Object> config) {
        // TODO: Implement system configuration persistence
    }

    public String createSystemBackup() {
        // TODO: Implement backup functionality
        return "backup_" + LocalDateTime.now().toString();
    }

    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();

        health.put("status", "UP");
        health.put("database", "UP");
        health.put("memory", Runtime.getRuntime().freeMemory());
        health.put("uptime", System.currentTimeMillis());

        return health;
    }
}
