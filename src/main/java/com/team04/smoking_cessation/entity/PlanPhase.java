package com.team04.smoking_cessation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "plan_phases")
public class PlanPhase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quit_plan_id", nullable = false)
    private QuitPlan quitPlan;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer phaseOrder;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer targetCigarettes;

    @Column(columnDefinition = "TEXT")
    private String dailyGoals; // JSON array of daily goals

    @Enumerated(EnumType.STRING)
    private PhaseStatus status = PhaseStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public PlanPhase() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public QuitPlan getQuitPlan() { return quitPlan; }
    public void setQuitPlan(QuitPlan quitPlan) { this.quitPlan = quitPlan; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getPhaseOrder() { return phaseOrder; }
    public void setPhaseOrder(Integer phaseOrder) { this.phaseOrder = phaseOrder; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getTargetCigarettes() { return targetCigarettes; }
    public void setTargetCigarettes(Integer targetCigarettes) { this.targetCigarettes = targetCigarettes; }

    public String getDailyGoals() { return dailyGoals; }
    public void setDailyGoals(String dailyGoals) { this.dailyGoals = dailyGoals; }

    public PhaseStatus getStatus() { return status; }
    public void setStatus(PhaseStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum PhaseStatus {
        PENDING, ACTIVE, COMPLETED, SKIPPED
    }
}
