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
@Table(name = "membership_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "max_coach_sessions")
    private Integer maxCoachSessions;

    @Column(name = "has_premium_features")
    private Boolean hasPremiumFeatures = false;

    @Column(name = "has_personalized_plan")
    private Boolean hasPersonalizedPlan = false;

    @Column(name = "has_progress_analytics")
    private Boolean hasProgressAnalytics = false;

    @Enumerated(EnumType.STRING)
    private PackageStatus status = PackageStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PackageStatus {
        ACTIVE, INACTIVE
    }
}
