package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.Achievement;
import com.smokingcessation.platform.entity.UserAchievement;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.ProgressTracking;
import com.smokingcessation.platform.repository.AchievementRepository;
import com.smokingcessation.platform.repository.UserAchievementRepository;
import com.smokingcessation.platform.repository.ProgressTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final ProgressTrackingRepository progressTrackingRepository;
    private final NotificationService notificationService;

    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    public List<Achievement> getAchievementsByType(Achievement.AchievementType type) {
        return achievementRepository.findByType(type);
    }

    public List<UserAchievement> getUserAchievements(Long userId) {
        return userAchievementRepository.findByUserIdOrderByEarnedAtDesc(userId);
    }

    public List<UserAchievement> getSharedAchievements() {
        return userAchievementRepository.findSharedAchievementsOrderBySharedAtDesc();
    }

    // Kiểm tra và trao huy hiệu cho user dựa trên tiến trình
    public void checkAndAwardAchievements(Long userId) {
        User user = new User();
        user.setId(userId);

        // Kiểm tra huy hiệu theo số ngày không hút thuốc
        checkDaysSmokeFreeAchievements(user);

        // Kiểm tra huy hiệu theo tiền tiết kiệm
        checkMoneySavedAchievements(user);

        // Kiểm tra huy hiệu theo chuỗi ngày liên tiếp
        checkStreakAchievements(user);
    }

    private void checkDaysSmokeFreeAchievements(User user) {
        Integer maxStreak = progressTrackingRepository.findMaxStreakByUserId(user.getId()).orElse(0);

        List<Achievement> daysAchievements = achievementRepository.findByTypeOrderByTargetValueAsc(
            Achievement.AchievementType.DAYS_SMOKE_FREE);

        for (Achievement achievement : daysAchievements) {
            if (maxStreak >= achievement.getTargetValue() &&
                !userAchievementRepository.existsByUserAndAchievement(user, achievement)) {

                awardAchievement(user, achievement);
            }
        }
    }

    private void checkMoneySavedAchievements(User user) {
        Double totalMoneySaved = progressTrackingRepository.getTotalMoneySavedByUserId(user.getId()).orElse(0.0);

        List<Achievement> moneyAchievements = achievementRepository.findByType(Achievement.AchievementType.MONEY_SAVED);

        for (Achievement achievement : moneyAchievements) {
            if (achievement.getTargetMoney() != null &&
                BigDecimal.valueOf(totalMoneySaved).compareTo(achievement.getTargetMoney()) >= 0 &&
                !userAchievementRepository.existsByUserAndAchievement(user, achievement)) {

                awardAchievement(user, achievement);
            }
        }
    }

    private void checkStreakAchievements(User user) {
        // Lấy tiến trình mới nhất để kiểm tra chuỗi hiện tại
        List<ProgressTracking> recentProgress = progressTrackingRepository.findByUserIdOrderByTrackingDateDesc(user.getId());

        if (!recentProgress.isEmpty()) {
            Integer currentStreak = recentProgress.get(0).getCurrentStreak();

            List<Achievement> streakAchievements = achievementRepository.findByType(Achievement.AchievementType.STREAK);

            for (Achievement achievement : streakAchievements) {
                if (currentStreak >= achievement.getTargetValue() &&
                    !userAchievementRepository.existsByUserAndAchievement(user, achievement)) {

                    awardAchievement(user, achievement);
                }
            }
        }
    }

    private void awardAchievement(User user, Achievement achievement) {
        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(user);
        userAchievement.setAchievement(achievement);
        userAchievement.setEarnedAt(LocalDateTime.now());

        userAchievementRepository.save(userAchievement);

        // Gửi thông báo cho user
        notificationService.sendAchievementNotification(user.getId(), achievement);
    }

    public UserAchievement shareAchievement(Long userAchievementId) {
        UserAchievement userAchievement = userAchievementRepository.findById(userAchievementId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy huy hiệu"));

        userAchievement.setIsShared(true);
        userAchievement.setSharedAt(LocalDateTime.now());

        return userAchievementRepository.save(userAchievement);
    }

    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    public Achievement updateAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    public void deleteAchievement(Long achievementId) {
        achievementRepository.deleteById(achievementId);
    }

    public Long getUserAchievementCount(Long userId) {
        return userAchievementRepository.countByUserId(userId);
    }
}
