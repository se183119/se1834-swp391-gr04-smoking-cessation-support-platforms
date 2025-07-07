package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.DailyLog;
import com.team04.smoking_cessation.entity.QuitPlan;
import com.team04.smoking_cessation.entity.AccountStatus;
import com.team04.smoking_cessation.repository.UserRepository;
import com.team04.smoking_cessation.repository.DailyLogRepository;
import com.team04.smoking_cessation.repository.QuitPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DailyLogService dailyLogService;

    /**
     * Gửi thông báo hàng ngày cho tất cả users có active quit plan
     * Chạy mỗi ngày lúc 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDailyMotivationalNotifications() {
        List<User> activeUsers = userRepository.findByStatus(AccountStatus.ACTIVE);
        
        for (User user : activeUsers) {
            try {
                // Kiểm tra xem user có active quit plan và email verified không
                if (user.getEmailVerified() && quitPlanRepository.findActiveQuitPlanByUser(user).isPresent()) {
                    sendDailyMotivationalMessage(user);
                }
            } catch (Exception e) {
                // Log error nhưng không dừng process
                System.err.println("Failed to send notification to " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Gửi thông báo hàng tuần với tóm tắt tiến độ
     * Chạy mỗi Chủ nhật lúc 10:00 AM
     */
    @Scheduled(cron = "0 0 10 ? * SUN")
    public void sendWeeklyProgressNotifications() {
        List<User> activeUsers = userRepository.findByStatus(AccountStatus.ACTIVE);
        
        for (User user : activeUsers) {
            try {
                if (user.getEmailVerified() && quitPlanRepository.findActiveQuitPlanByUser(user).isPresent()) {
                    sendWeeklyProgressReport(user);
                }
            } catch (Exception e) {
                System.err.println("Failed to send weekly report to " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Gửi thông báo milestone (7 ngày, 30 ngày, 90 ngày)
     * Chạy mỗi ngày lúc 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendMilestoneNotifications() {
        List<User> activeUsers = userRepository.findByStatus(AccountStatus.ACTIVE);
        
        for (User user : activeUsers) {
            try {
                if (user.getEmailVerified()) {
                    Long smokeFreeDays = dailyLogRepository.countSmokeFreeDaysByUser(user);
                    sendMilestoneNotification(user, smokeFreeDays);
                }
            } catch (Exception e) {
                System.err.println("Failed to send milestone notification to " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Gửi thông báo nhắc nhở ghi log hàng ngày
     * Chạy mỗi ngày lúc 7:00 PM nếu user chưa ghi log hôm nay
     */
    @Scheduled(cron = "0 0 19 * * ?")
    public void sendDailyLogReminders() {
        List<User> activeUsers = userRepository.findByStatus(AccountStatus.ACTIVE);
        
        for (User user : activeUsers) {
            try {
                if (user.getEmailVerified()) {
                    var todayLog = dailyLogRepository.findByUserAndLogDate(user, LocalDate.now());
                    if (todayLog.isEmpty()) {
                        sendDailyLogReminder(user);
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to send log reminder to " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    private void sendDailyMotivationalMessage(User user) {
        String motivationalMessage = dailyLogService.generateMotivationalMessage(user.getEmail());
        
        String subject = "Your Daily Motivation - Stay Strong! 💪";
        String message = buildMotivationalEmail(user, motivationalMessage);
        
        emailService.sendMotivationalMessage(user.getEmail(), message);
    }

    private void sendWeeklyProgressReport(User user) {
        Map<String, Object> stats = dailyLogService.getUserStatistics(user.getEmail());
        
        String subject = "Your Weekly Progress Report 📊";
        String message = buildWeeklyReportEmail(user, stats);
        
        emailService.sendMotivationalMessage(user.getEmail(), message);
    }

    private void sendMilestoneNotification(User user, Long smokeFreeDays) {
        if (smokeFreeDays == 7 || smokeFreeDays == 30 || smokeFreeDays == 90 || smokeFreeDays == 365) {
            String subject = "🎉 Congratulations! You've Reached " + smokeFreeDays + " Days Smoke-Free!";
            String message = buildMilestoneEmail(user, smokeFreeDays);
            
            emailService.sendMotivationalMessage(user.getEmail(), message);
        }
    }

    private void sendDailyLogReminder(User user) {
        String subject = "Don't Forget to Log Your Progress Today! 📝";
        String message = buildLogReminderEmail(user);
        
        emailService.sendMotivationalMessage(user.getEmail(), message);
    }

    private String buildMotivationalEmail(User user, String motivationalMessage) {
        StringBuilder email = new StringBuilder();
        email.append("Hello ").append(user.getFullName()).append(",\n\n");
        email.append("Here's your daily dose of motivation:\n\n");
        email.append(motivationalMessage).append("\n\n");
        email.append("Remember why you started this journey. Every smoke-free day is a victory!\n\n");
        email.append("Stay strong and keep going!\n");
        email.append("Your Smoking Cessation Support Team");
        
        return email.toString();
    }

    private String buildWeeklyReportEmail(User user, Map<String, Object> stats) {
        StringBuilder email = new StringBuilder();
        email.append("Hello ").append(user.getFullName()).append(",\n\n");
        email.append("Here's your weekly progress report:\n\n");
        
        Long smokeFreeDays = (Long) stats.get("smokeFreeDays");
        Double moneySaved = (Double) stats.get("moneySaved");
        
        email.append("📈 Smoke-free days: ").append(smokeFreeDays).append(" days\n");
        email.append("💰 Money saved: $").append(String.format("%.2f", moneySaved)).append("\n");
        
        if (stats.containsKey("healthTrends")) {
            Map<String, Object> healthTrends = (Map<String, Object>) stats.get("healthTrends");
            if (healthTrends.containsKey("averageMood")) {
                email.append("😊 Average mood: ").append(healthTrends.get("averageMood")).append("/10\n");
            }
        }
        
        email.append("\nKeep up the amazing work! You're making incredible progress.\n\n");
        email.append("Your Smoking Cessation Support Team");
        
        return email.toString();
    }

    private String buildMilestoneEmail(User user, Long smokeFreeDays) {
        StringBuilder email = new StringBuilder();
        email.append("Hello ").append(user.getFullName()).append(",\n\n");
        email.append("🎉 CONGRATULATIONS! 🎉\n\n");
        email.append("You've reached an incredible milestone: ").append(smokeFreeDays).append(" days smoke-free!\n\n");
        
        if (smokeFreeDays == 7) {
            email.append("One week smoke-free! Your body is already starting to heal.\n");
        } else if (smokeFreeDays == 30) {
            email.append("One month smoke-free! Your lung function is improving significantly.\n");
        } else if (smokeFreeDays == 90) {
            email.append("Three months smoke-free! Your risk of heart disease has dropped dramatically.\n");
        } else if (smokeFreeDays == 365) {
            email.append("ONE YEAR SMOKE-FREE! You're a true inspiration to others!\n");
        }
        
        email.append("\nYou're an inspiration to everyone around you. Keep going!\n\n");
        email.append("Your Smoking Cessation Support Team");
        
        return email.toString();
    }

    private String buildLogReminderEmail(User user) {
        StringBuilder email = new StringBuilder();
        email.append("Hello ").append(user.getFullName()).append(",\n\n");
        email.append("Don't forget to log your progress today!\n\n");
        email.append("Taking a moment to record your journey helps you:\n");
        email.append("• Track your progress\n");
        email.append("• Stay motivated\n");
        email.append("• Identify patterns\n");
        email.append("• Celebrate victories\n\n");
        email.append("Log your progress now and keep your momentum going!\n\n");
        email.append("Your Smoking Cessation Support Team");
        
        return email.toString();
    }

    /**
     * Gửi thông báo tùy chỉnh cho user cụ thể
     */
    public void sendCustomNotification(String userEmail, String subject, String message) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        emailService.sendMotivationalMessage(user.getEmail(), message);
    }

    /**
     * Gửi thông báo cho tất cả premium users
     */
    public void sendNotificationToPremiumUsers(String subject, String message) {
        // TODO: Implement filter for premium users only
        List<User> activeUsers = userRepository.findByStatus(AccountStatus.ACTIVE);
        
        for (User user : activeUsers) {
            try {
                if (user.getEmailVerified()) {
                    emailService.sendMotivationalMessage(user.getEmail(), message);
                }
            } catch (Exception e) {
                System.err.println("Failed to send notification to " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }
} 