package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "smoking_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmokingStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "cigarettes_per_day")
    private Integer cigarettesPerDay;

    @Column(name = "smoking_frequency")
    private String smokingFrequency; // hourly, daily patterns

    @Column(name = "cigarette_price", precision = 10, scale = 2)
    private BigDecimal cigarettePrice; // giá 1 bao thuốc

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "years_smoking")
    private Integer yearsSmoking;

    @Column(name = "attempts_to_quit")
    private Integer attemptsToQuit = 0;

    @Column(name = "triggers", columnDefinition = "TEXT")
    private String triggers; // các tình huống dễ hút thuốc

    @Enumerated(EnumType.STRING)
    private MotivationLevel motivationLevel;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum MotivationLevel {
        LOW, MEDIUM, HIGH, VERY_HIGH
    }
}
