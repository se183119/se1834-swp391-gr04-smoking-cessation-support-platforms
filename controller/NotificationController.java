package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.Notification;
import com.smokingcessation.platform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    // Lấy tất cả thông báo của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // Lấy thông báo chưa đọc
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unreadNotifications);
    }

    // Lấy số lượng thông báo chưa đọc
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        Long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    // Đánh dấu thông báo đã đọc
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Đánh dấu tất cả thông báo đã đọc
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Tạo thông báo động viên hàng ngày
    @PostMapping("/user/{userId}/daily-motivation")
    public ResponseEntity<Void> createDailyMotivation(@PathVariable Long userId) {
        try {
            notificationService.createDailyMotivation(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Tạo thông báo nhắc nhở lý do cai thuốc
    @PostMapping("/user/{userId}/quit-reminder")
    public ResponseEntity<Void> createQuitReasonReminder(@PathVariable Long userId,
                                                        @RequestBody QuitReminderRequest request) {
        try {
            notificationService.createQuitReasonReminder(userId, request.getQuitReason());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Gửi thông báo cột mốc quan trọng
    @PostMapping("/user/{userId}/milestone")
    public ResponseEntity<Void> sendMilestoneNotification(@PathVariable Long userId,
                                                         @RequestBody MilestoneRequest request) {
        try {
            notificationService.sendMilestoneNotification(userId, request.getMilestone(), request.getDescription());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lên lịch thông báo định kỳ
    @PostMapping("/user/{userId}/schedule")
    public ResponseEntity<Void> schedulePeriodicNotifications(@PathVariable Long userId,
                                                             @RequestBody ScheduleRequest request) {
        try {
            notificationService.schedulePeriodicNotifications(userId, request.getFrequency());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: Xử lý gửi thông báo đã lên lịch
    @PostMapping("/process-pending")
    public ResponseEntity<Void> processPendingNotifications() {
        try {
            notificationService.processPendingNotifications();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTOs
    public static class QuitReminderRequest {
        private String quitReason;

        public String getQuitReason() { return quitReason; }
        public void setQuitReason(String quitReason) { this.quitReason = quitReason; }
    }

    public static class MilestoneRequest {
        private String milestone;
        private String description;

        public String getMilestone() { return milestone; }
        public void setMilestone(String milestone) { this.milestone = milestone; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class ScheduleRequest {
        private Notification.NotificationFrequency frequency;

        public Notification.NotificationFrequency getFrequency() { return frequency; }
        public void setFrequency(Notification.NotificationFrequency frequency) { this.frequency = frequency; }
    }
}
