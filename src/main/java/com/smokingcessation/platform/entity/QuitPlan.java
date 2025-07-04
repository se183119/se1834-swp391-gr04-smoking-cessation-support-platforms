package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quit_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(name = "quit_reason", columnDefinition = "TEXT")
    private String quitReason; // lý do cai thuốc

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // thời điểm bắt đầu

    @Column(name = "target_quit_date")
    private LocalDate targetQuitDate; // thời điểm dự kiến cai được thuốc

    @Enumerated(EnumType.STRING)
    private QuitMethod quitMethod;

    @OneToMany(mappedBy = "quitPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuitPlanPhase> phases; // các giai đoạn

    @Enumerated(EnumType.STRING)
    private PlanStatus status = PlanStatus.ACTIVE;

    @Column(name = "is_system_generated")
    private Boolean isSystemGenerated = false; // do hệ thống tự phát sinh

    @Column(name = "customization_notes", columnDefinition = "TEXT")
    private String customizationNotes; // ghi chú tùy chỉnh của user

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum QuitMethod {
        COLD_TURKEY, GRADUAL_REDUCTION, NICOTINE_REPLACEMENT, MEDICATION, BEHAVIORAL_THERAPY
    }

    public enum PlanStatus {
        ACTIVE, PAUSED, COMPLETED, CANCELLED
    }
}
