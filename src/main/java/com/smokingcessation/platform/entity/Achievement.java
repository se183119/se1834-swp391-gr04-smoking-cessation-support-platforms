package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "badge_icon")
    private String badgeIcon; // đường dẫn icon huy hiệu

    @Enumerated(EnumType.STRING)
    private AchievementType type;

    @Column(name = "target_value")
    private Integer targetValue; // mục tiêu (số ngày, số điếu thuốc...)

    @Column(name = "target_money", precision = 10, scale = 2)
    private BigDecimal targetMoney; // mục tiêu tiền tiết kiệm

    @Enumerated(EnumType.STRING)
    private AchievementLevel level;

    @Column(name = "points_awarded")
    private Integer pointsAwarded = 0;

    @Column(name = "is_shareable")
    private Boolean isShareable = true; // có thể chia sẻ không

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum AchievementType {
        DAYS_SMOKE_FREE, MONEY_SAVED, HEALTH_MILESTONE, STREAK, PARTICIPATION
    }

    public enum AchievementLevel {
        BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
    }
}
