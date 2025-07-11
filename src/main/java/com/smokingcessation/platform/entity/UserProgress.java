package com.smokingcessation.platform.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="user_progress",
        uniqueConstraints=@UniqueConstraint(columnNames={"quit_plan_id","log_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="quit_plan_id", nullable=false)
    @JsonBackReference
    private QuitPlan quitPlan;
    @Column(name="log_date", nullable=false) private LocalDate logDate;
    @Column(nullable=false) private Integer smoked;
    @Column(length=255) private String note;
}