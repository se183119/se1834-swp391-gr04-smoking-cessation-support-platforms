package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="quit_plan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuitPlan {
    @Id @GeneratedValue private Long id;
    @ManyToOne @JoinColumn(name="user_id", nullable=false)
    private User user;
    @Column(name="start_date", nullable=false) private LocalDate startDate;
    @Column(name="quit_months", nullable=false, precision=4, scale=1)
    private BigDecimal quitMonths;

    private boolean isDone = false;

    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy="quitPlan", cascade=CascadeType.ALL, orphanRemoval=true)
    @OrderBy("stepIndex ASC")
    private List<PlanMilestone> milestones = new ArrayList<>();

    @OneToMany(mappedBy="quitPlan", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<UserProgress> progresses = new ArrayList<>();
}