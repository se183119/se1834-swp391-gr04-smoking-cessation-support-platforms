package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tracking_date", nullable = false)
    private LocalDate trackingDate;

    @Column(name = "cigarettes_smoked")
    private Integer cigarettesSmoked = 0; // số điếu đã hút trong ngày

    @Column(name = "money_saved", precision = 10, scale = 2)
    private BigDecimal moneySaved = BigDecimal.ZERO; // tiền tiết kiệm được

    @Column(name = "days_smoke_free")
    private Integer daysSmokeFreePeak = 0; // số ngày không hút tối đa

    @Column(name = "current_streak")
    private Integer currentStreak = 0; // chuỗi ngày không hút hiện tại

    @Enumerated(EnumType.STRING)
    @Column(name = "health_status")
    private HealthStatus healthStatus; // tình trạng sức khỏe

    @Column(name = "mood_score") // thang điểm tâm trạng 1-10
    private Integer moodScore;

    @Column(name = "craving_intensity") // mức độ thèm thuốc 1-10
    private Integer cravingIntensity;

    @Column(name = "exercise_minutes")
    private Integer exerciseMinutes = 0;

    @Column(name = "sleep_hours")
    private Double sleepHours;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // ghi chú cá nhân

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum HealthStatus {
        POOR, FAIR, GOOD, VERY_GOOD, EXCELLENT
    }
}
