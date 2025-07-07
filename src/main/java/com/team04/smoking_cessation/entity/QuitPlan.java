package com.team04.smoking_cessation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quit_plans")
public class QuitPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private QuitMethod quitMethod;

    private LocalDate quitDate;
    private LocalDate targetDate;

    @Column(columnDefinition = "TEXT")
    private String personalReasons; // JSON array of reasons

    @Column(columnDefinition = "TEXT")
    private String copingStrategies; // JSON array of strategies

    @Column(columnDefinition = "TEXT")
    private String rewardSystem; // JSON array of rewards

    @Enumerated(EnumType.STRING)
    private PlanStatus status = PlanStatus.ACTIVE;

    private Integer dailyCigaretteLimit;
    private Boolean isAiGenerated = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "quitPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanPhase> phases;

    // Constructors
    public QuitPlan() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public QuitMethod getQuitMethod() { return quitMethod; }
    public void setQuitMethod(QuitMethod quitMethod) { this.quitMethod = quitMethod; }

    public LocalDate getQuitDate() { return quitDate; }
    public void setQuitDate(LocalDate quitDate) { this.quitDate = quitDate; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public String getPersonalReasons() { return personalReasons; }
    public void setPersonalReasons(String personalReasons) { this.personalReasons = personalReasons; }

    public String getCopingStrategies() { return copingStrategies; }
    public void setCopingStrategies(String copingStrategies) { this.copingStrategies = copingStrategies; }

    public String getRewardSystem() { return rewardSystem; }
    public void setRewardSystem(String rewardSystem) { this.rewardSystem = rewardSystem; }

    public PlanStatus getStatus() { return status; }
    public void setStatus(PlanStatus status) { this.status = status; }

    public Integer getDailyCigaretteLimit() { return dailyCigaretteLimit; }
    public void setDailyCigaretteLimit(Integer dailyCigaretteLimit) { this.dailyCigaretteLimit = dailyCigaretteLimit; }

    public Boolean getIsAiGenerated() { return isAiGenerated; }
    public void setIsAiGenerated(Boolean isAiGenerated) { this.isAiGenerated = isAiGenerated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<PlanPhase> getPhases() { return phases; }
    public void setPhases(List<PlanPhase> phases) { this.phases = phases; }

    // Enums
    public enum QuitMethod {
        COLD_TURKEY, GRADUAL_REDUCTION, NICOTINE_REPLACEMENT, MEDICATION
    }

    public enum PlanStatus {
        ACTIVE, COMPLETED, PAUSED, FAILED
    }
}
