package com.smokingcessation.platform.service;

import com.smokingcessation.platform.config.EmailService;
import com.smokingcessation.platform.entity.Achievement;
import com.smokingcessation.platform.entity.UserAchievement;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.ProgressTracking;
import com.smokingcessation.platform.repository.AchievementRepository;
import com.smokingcessation.platform.repository.UserAchievementRepository;
import com.smokingcessation.platform.repository.ProgressTrackingRepository;
import com.smokingcessation.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
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

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    public void createUserAchievement(Long userId, Long achievementId ) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));

        Achievement achievement = achievementRepository.findById(achievementId).get();

        if (userAchievementRepository.existsByUserAndAchievement(user, achievement)) {
            return; // Huy hiệu đã tồn tại
        }

        UserAchievement userAchievement = new UserAchievement();
        userAchievement.setUser(user);
        userAchievement.setAchievement(achievement);
        userAchievement.setEarnedAt(LocalDateTime.now());
        userAchievement.setIsShared(false);
        userAchievement.setCreatedAt(LocalDateTime.now());

        userAchievementRepository.save(userAchievement);


        // Gửi thông báo cho người dùng
        try {
            String subject = "Chúc mừng! Bạn vừa có một thành tích mới!";
            String htmlContent = String.format("""
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width,initial-scale=1.0">
  <title>Chúc mừng bạn đã đạt thành tích!</title>
  <style>
    body {
      margin: 0;
      padding: 0;
      background-color: #f4f4f4;
      font-family: Arial, sans-serif;
      color: #333333;
    }
    .wrapper {
      width: 100%%;
      table-layout: fixed;
      background-color: #f4f4f4;
      padding-bottom: 40px;
    }
    .main {
      background-color: #ffffff;
      margin: 0 auto;
      width: 100%%;
      max-width: 600px;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    .header {
      background-color: #007bff;
      padding: 20px;
      text-align: center;
      color: #ffffff;
    }
    .header h1 {
      margin: 0;
      font-size: 24px;
    }
    .content {
      padding: 30px 20px;
      line-height: 1.6;
    }
    .content p {
      margin: 0 0 15px;
    }
    .btn {
      display: inline-block;
      padding: 12px 24px;
      background-color: #28a745;
      color: #ffffff !important;
      text-decoration: none;
      border-radius: 4px;
      font-size: 16px;
    }
    .footer {
      background-color: #f1f1f1;
      text-align: center;
      padding: 15px;
      font-size: 12px;
      color: #777777;
    }
  </style>
</head>
<body>
  <center class="wrapper">
    <table class="main" width="100%%">
      <!-- Header -->
      <tr>
        <td class="header">
          <h1>Chúc mừng, %s!</h1>
        </td>
      </tr>
      <!-- Content -->
      <tr>
        <td class="content">
          <p>Bạn vừa đạt được thành tích:<br/><strong>%s</strong></p>
          <p>Chúng tôi rất tự hào về sự nỗ lực của bạn. Hãy tiếp tục khám phá và chinh phục những cột mốc mới!</p>
          <p style="text-align: center;">
            <a href="%s" class="btn">Đăng nhập và xem chi tiết</a>
          </p>
          <p>Nếu bạn có bất kỳ câu hỏi nào, hãy phản hồi email này hoặc liên hệ với đội ngũ hỗ trợ.</p>
        </td>
      </tr>
      <!-- Footer -->
      <tr>
        <td class="footer">
          © %d Smoking Team. Tất cả quyền được bảo lưu.
        </td>
      </tr>
    </table>
  </center>
</body>
</html>
""",
                    user.getFullName(),                // %s đầu tiên: tên người dùng
                    achievement.getName(),             // %s thứ hai: tên thành tích
                    "https://scsp.autopass.blog/login", // %s thứ ba: URL đăng nhập
                    Year.now().getValue()              // %d: năm hiện tại
            );

            emailService.sendHtmlEmail(user.getEmail(), subject, htmlContent);
        } catch (Exception e) {
            System.err.println("Không thể gửi email xác nhận: " + e.getMessage());
        }

        notificationService.sendAchievementNotification(user.getId(), achievement);
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
