package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.Notification;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.Achievement;
import com.smokingcessation.platform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // Các thông điệp động viên mẫu
    private final List<String> motivationMessages = Arrays.asList(
        "Hôm nay là một ngày tuyệt vời để tiếp tục hành trình cai thuốc!",
        "Mỗi ngày không hút thuốc là một chiến thắng!",
        "Sức khỏe của bạn đang cải thiện từng ngày!",
        "Bạn mạnh mẽ hơn cơn thèm thuốc!",
        "Hãy nghĩ về những người bạn yêu thương!"
    );

    private final List<String> reminderMessages = Arrays.asList(
        "Nhớ lại lý do bạn quyết định cai thuốc nhé!",
        "Gia đình bạn tự hào về quyết định này!",
        "Tiền tiết kiệm được có thể dùng cho những điều ý nghĩa hơn!",
        "Sức khỏe là tài sản quý giá nhất!"
    );

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        unreadNotifications.forEach(notification -> notification.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    // Gửi thông báo huy hiệu
    public void sendAchievementNotification(Long userId, Achievement achievement) {
        User user = new User();
        user.setId(userId);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("🏆 Chúc mừng! Bạn đã đạt được huy hiệu mới!");
        notification.setMessage("Bạn vừa nhận được huy hiệu: " + achievement.getName() +
                               ". " + achievement.getDescription());
        notification.setType(Notification.NotificationType.ACHIEVEMENT);
        notification.setFrequency(Notification.NotificationFrequency.ONCE);
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    // Tạo thông báo động viên hàng ngày
    public void createDailyMotivation(Long userId) {
        User user = new User();
        user.setId(userId);

        String randomMessage = motivationMessages.get(
            (int) (Math.random() * motivationMessages.size()));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("💪 Động viên hàng ngày");
        notification.setMessage(randomMessage);
        notification.setType(Notification.NotificationType.MOTIVATION);
        notification.setFrequency(Notification.NotificationFrequency.DAILY);
        notification.setScheduledTime(LocalDateTime.now().plusHours(1));

        notificationRepository.save(notification);
    }

    // Tạo thông báo nhắc nhở lý do cai thuốc
    public void createQuitReasonReminder(Long userId, String quitReason) {
        User user = new User();
        user.setId(userId);

        String randomReminder = reminderMessages.get(
            (int) (Math.random() * reminderMessages.size()));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("🎯 Nhắc nhở lý do cai thuốc");
        notification.setMessage(randomReminder + " Lý do của bạn: " + quitReason);
        notification.setType(Notification.NotificationType.REMINDER);
        notification.setFrequency(Notification.NotificationFrequency.WEEKLY);
        notification.setScheduledTime(LocalDateTime.now().plusDays(1));

        notificationRepository.save(notification);
    }

    // Gửi thông báo cột mốc quan trọng
    public void sendMilestoneNotification(Long userId, String milestone, String description) {
        User user = new User();
        user.setId(userId);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("🎉 Cột mốc quan trọng!");
        notification.setMessage(milestone + " - " + description);
        notification.setType(Notification.NotificationType.MILESTONE);
        notification.setFrequency(Notification.NotificationFrequency.ONCE);
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    // Gửi tin nhắn từ coach
    public void sendCoachMessage(Long userId, String message, String coachName) {
        User user = new User();
        user.setId(userId);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("💬 Tin nhắn từ Coach " + coachName);
        notification.setMessage(message);
        notification.setType(Notification.NotificationType.COACH_MESSAGE);
        notification.setFrequency(Notification.NotificationFrequency.ONCE);
        notification.setSentAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    // Lên lịch thông báo định kỳ
    public void schedulePeriodicNotifications(Long userId, Notification.NotificationFrequency frequency) {
        User user = new User();
        user.setId(userId);

        LocalDateTime scheduledTime = calculateNextScheduledTime(frequency);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("🔔 Thông báo định kỳ");
        notification.setMessage("Đây là thông báo định kỳ để nhắc nhở bạn về hành trình cai thuốc!");
        notification.setType(Notification.NotificationType.REMINDER);
        notification.setFrequency(frequency);
        notification.setScheduledTime(scheduledTime);

        notificationRepository.save(notification);
    }

    private LocalDateTime calculateNextScheduledTime(Notification.NotificationFrequency frequency) {
        LocalDateTime now = LocalDateTime.now();
        switch (frequency) {
            case DAILY:
                return now.plusDays(1);
            case WEEKLY:
                return now.plusWeeks(1);
            case MONTHLY:
                return now.plusMonths(1);
            default:
                return now.plusHours(1);
        }
    }

    // Xử lý gửi thông báo đã lên lịch
    public void processPendingNotifications() {
        List<Notification> pendingNotifications = notificationRepository
            .findPendingNotifications(LocalDateTime.now());

        for (Notification notification : pendingNotifications) {
            // Gửi thông báo (có thể tích hợp với email, SMS, push notification)
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);

            // Tạo lại thông báo định kỳ nếu cần
            if (notification.getFrequency() != Notification.NotificationFrequency.ONCE) {
                scheduleNextNotification(notification);
            }
        }
    }

    private void scheduleNextNotification(Notification originalNotification) {
        Notification nextNotification = new Notification();
        nextNotification.setUser(originalNotification.getUser());
        nextNotification.setTitle(originalNotification.getTitle());
        nextNotification.setMessage(originalNotification.getMessage());
        nextNotification.setType(originalNotification.getType());
        nextNotification.setFrequency(originalNotification.getFrequency());
        nextNotification.setScheduledTime(calculateNextScheduledTime(originalNotification.getFrequency()));

        notificationRepository.save(nextNotification);
    }
}
