package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "quit_plan_phases")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitPlanPhase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quit_plan_id", nullable = false)
    private QuitPlan quitPlan;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "phase_number", nullable = false)
    private Integer phaseNumber; // thứ tự giai đoạn

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "target_cigarettes_per_day")
    private Integer targetCigarettesPerDay; // mục tiêu số điếu thuốc/ngày

    @Column(name = "goals", columnDefinition = "TEXT")
    private String goals; // mục tiêu cụ thể của giai đoạn

    @Enumerated(EnumType.STRING)
    private PhaseStatus status = PhaseStatus.PENDING;

    public enum PhaseStatus {
        PENDING, IN_PROGRESS, COMPLETED, SKIPPED
    }
}
