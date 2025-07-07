package com.team04.smoking_cessation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_logs")
public class DailyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate logDate;
    private Integer cigarettesSmoked = 0;
    private Integer cravingsCount = 0;
    private Integer moodLevel; // 1-10 scale
    private Integer stressLevel; // 1-10 scale
    private Integer energyLevel; // 1-10 scale
    private Double weight;
    private String bloodPressure;

    @Column(columnDefinition = "TEXT")
    private String triggers; // JSON array of triggers experienced

    @Column(columnDefinition = "TEXT")
    private String symptoms; // JSON array of symptoms

    @Column(columnDefinition = "TEXT")
    private String activities; // JSON array of alternative activities

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Boolean isSmokeFree = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public DailyLog() {}

    public DailyLog(User user, LocalDate logDate) {
        this.user = user;
        this.logDate = logDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    public Integer getCigarettesSmoked() { return cigarettesSmoked; }
    public void setCigarettesSmoked(Integer cigarettesSmoked) { this.cigarettesSmoked = cigarettesSmoked; }

    public Integer getCravingsCount() { return cravingsCount; }
    public void setCravingsCount(Integer cravingsCount) { this.cravingsCount = cravingsCount; }

    public Integer getMoodLevel() { return moodLevel; }
    public void setMoodLevel(Integer moodLevel) { this.moodLevel = moodLevel; }

    public Integer getStressLevel() { return stressLevel; }
    public void setStressLevel(Integer stressLevel) { this.stressLevel = stressLevel; }

    public Integer getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(Integer energyLevel) { this.energyLevel = energyLevel; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }

    public String getTriggers() { return triggers; }
    public void setTriggers(String triggers) { this.triggers = triggers; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getActivities() { return activities; }
    public void setActivities(String activities) { this.activities = activities; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getIsSmokeFree() { return isSmokeFree; }
    public void setIsSmokeFree(Boolean isSmokeFree) { this.isSmokeFree = isSmokeFree; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
