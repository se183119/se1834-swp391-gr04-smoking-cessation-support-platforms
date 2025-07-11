package com.smokingcessation.platform.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="plan_milestone",
        uniqueConstraints=@UniqueConstraint(columnNames={"quit_plan_id","step_index"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanMilestone {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne @JoinColumn(name="quit_plan_id", nullable=false)
    private QuitPlan quitPlan;
    @Column(name="step_index", nullable=false) private Integer stepIndex;
    @Column(name="day_offset", nullable=false) private Integer dayOffset;
    @Column(name="target_cigarettes", nullable=false) private Integer targetCigarettes;

}
