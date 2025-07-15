package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private NotificationFrequency frequency; // tần suất thông báo

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime; // thời gian lên lịch gửi

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum NotificationType {
        MOTIVATION, // thông điệp động viên
        REMINDER, // nhắc nhở lý do cai thuốc
        ACHIEVEMENT, // thông báo huy hiệu
        MILESTONE, // cột mốc quan trọng
        COACH_MESSAGE, // tin nhắn từ coach
        SYSTEM_UPDATE
    }

    public enum NotificationFrequency {
        ONCE, DAILY, WEEKLY, MONTHLY, CUSTOM
    }
}
