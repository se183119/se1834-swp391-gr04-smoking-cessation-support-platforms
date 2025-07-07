package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.*;
import com.team04.smoking_cessation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AchievementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private EmailService emailService;

    public List<Achievement> getUserAchievements(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return achievementRepository.findByUserOrderByEarnedAtDesc(user);
    }

    public List<Badge> getAllAvailableBadges() {
        return badgeRepository.findByIsActiveTrueOrderByRequiredValueAsc();
    }

    public Map<String, Object> getAchievementProgress(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> progress = new HashMap<>();

        // Get current achievements
        List<Achievement> userAchievements = achievementRepository.findByUserOrderByEarnedAtDesc(user);
        progress.put("totalAchievements", userAchievements.size());

        // Get smoke-free days for progress calculation
        Long smokeFreeDays = dailyLogRepository.countSmokeFreeDaysByUser(user);
        progress.put("smokeFreeDays", smokeFreeDays);

        // Calculate money saved
        Double moneySaved = smokeFreeDays * 5.0; // Assume $5 per day
        progress.put("moneySaved", moneySaved);

        // Get next achievable badges
        List<Badge> allBadges = badgeRepository.findByIsActiveTrueOrderByRequiredValueAsc();
        List<Long> earnedBadgeIds = userAchievements.stream()
            .map(achievement -> achievement.getBadge().getId())
            .collect(Collectors.toList());

        List<Map<String, Object>> nextBadges = allBadges.stream()
            .filter(badge -> !earnedBadgeIds.contains(badge.getId()))
            .limit(3)
            .map(badge -> {
                Map<String, Object> badgeInfo = new HashMap<>();
                badgeInfo.put("id", badge.getId());
                badgeInfo.put("name", badge.getName());
                badgeInfo.put("description", badge.getDescription());
                badgeInfo.put("requiredValue", badge.getRequiredValue());
                badgeInfo.put("type", badge.getType());

                // Calculate progress toward this badge
                double currentProgress = calculateProgressTowardBadge(badge, smokeFreeDays, moneySaved);
                badgeInfo.put("progress", Math.min(100.0, currentProgress));

                return badgeInfo;
            })
            .collect(Collectors.toList());

        progress.put("nextBadges", nextBadges);

        return progress;
    }

    public List<Achievement> checkAndAwardAchievements(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<Achievement> newAchievements = new ArrayList<>();
        List<Badge> allBadges = badgeRepository.findByIsActiveTrueOrderByRequiredValueAsc();

        // Get user's existing achievements
        List<Long> earnedBadgeIds = achievementRepository.findByUserOrderByEarnedAtDesc(user)
            .stream()
            .map(achievement -> achievement.getBadge().getId())
            .collect(Collectors.toList());

        // Get current stats
        Long smokeFreeDays = dailyLogRepository.countSmokeFreeDaysByUser(user);
        Double moneySaved = smokeFreeDays * 5.0;

        for (Badge badge : allBadges) {
            if (!earnedBadgeIds.contains(badge.getId())) {
                boolean shouldAward = false;

                switch (badge.getType()) {
                    case TIME_BASED:
                        shouldAward = smokeFreeDays >= badge.getRequiredValue();
                        break;
                    case MONEY_BASED:
                        shouldAward = moneySaved >= badge.getRequiredValue();
                        break;
                    case HEALTH_BASED:
                        // Add health-based achievement logic here
                        shouldAward = smokeFreeDays >= badge.getRequiredValue();
                        break;
                    case SOCIAL_BASED:
                        // Add social-based achievement logic here
                        break;
                }

                if (shouldAward) {
                    Achievement achievement = new Achievement(user, badge);
                    achievementRepository.save(achievement);
                    newAchievements.add(achievement);

                    // Send notification email
                    emailService.sendAchievementNotification(user.getEmail(), badge.getName());
                }
            }
        }

        return newAchievements;
    }

    public void shareAchievement(String email, Long achievementId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Achievement achievement = achievementRepository.findById(achievementId)
            .orElseThrow(() -> new RuntimeException("Achievement not found"));

        if (!achievement.getUser().equals(user)) {
            throw new RuntimeException("Access denied: This achievement belongs to another user");
        }

        achievement.setIsShared(true);
        achievementRepository.save(achievement);
    }

    public List<Map<String, Object>> getAchievementLeaderboard() {
        List<Object[]> results = achievementRepository.getTopUsersByAchievementCount();

        return results.stream().map(result -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("userId", result[0]);
            entry.put("fullName", result[1]);
            entry.put("achievementCount", result[2]);
            entry.put("smokeFreeDays", result[3]);
            return entry;
        }).collect(Collectors.toList());
    }

    private double calculateProgressTowardBadge(Badge badge, Long smokeFreeDays, Double moneySaved) {
        switch (badge.getType()) {
            case TIME_BASED:
                return (smokeFreeDays.doubleValue() / badge.getRequiredValue()) * 100.0;
            case MONEY_BASED:
                return (moneySaved / badge.getRequiredValue()) * 100.0;
            case HEALTH_BASED:
                return (smokeFreeDays.doubleValue() / badge.getRequiredValue()) * 100.0;
            default:
                return 0.0;
        }
    }

    public void initializeDefaultBadges() {
        // Create default badges if they don't exist
        List<Badge> defaultBadges = Arrays.asList(
            new Badge("First Day", "Your first smoke-free day", Badge.BadgeType.TIME_BASED, Badge.BadgeCategory.SMOKE_FREE_DAYS, 1),
            new Badge("One Week Strong", "One week without smoking", Badge.BadgeType.TIME_BASED, Badge.BadgeCategory.SMOKE_FREE_DAYS, 7),
            new Badge("Monthly Milestone", "30 days smoke-free", Badge.BadgeType.TIME_BASED, Badge.BadgeCategory.SMOKE_FREE_DAYS, 30),
            new Badge("Quarter Champion", "90 days smoke-free", Badge.BadgeType.TIME_BASED, Badge.BadgeCategory.SMOKE_FREE_DAYS, 90),
            new Badge("Money Saver", "Saved $100", Badge.BadgeType.MONEY_BASED, Badge.BadgeCategory.MONEY_SAVED, 100),
            new Badge("Budget Master", "Saved $500", Badge.BadgeType.MONEY_BASED, Badge.BadgeCategory.MONEY_SAVED, 500),
            new Badge("Financial Freedom", "Saved $1000", Badge.BadgeType.MONEY_BASED, Badge.BadgeCategory.MONEY_SAVED, 1000)
        );

        for (Badge badge : defaultBadges) {
            if (!badgeRepository.existsByName(badge.getName())) {
                badgeRepository.save(badge);
            }
        }
    }
}
