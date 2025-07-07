package com.team04.smoking_cessation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "smoking_profiles")
public class SmokingProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Integer cigarettesPerDay;
    private String brand;
    private BigDecimal pricePerPack;
    private Integer cigarettesPerPack;
    private Integer yearsOfSmoking;
    private Integer previousQuitAttempts;

    @Column(columnDefinition = "TEXT")
    private String smokingTriggers; // JSON array of triggers

    @Column(columnDefinition = "TEXT")
    private String healthConcerns; // JSON array of health issues

    @Column(columnDefinition = "TEXT")
    private String motivations; // JSON array of quit motivations

    private Double currentWeight;
    private String bloodPressure;
    private Integer stressLevel; // 1-10 scale

    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Constructors
    public SmokingProfile() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getCigarettesPerDay() { return cigarettesPerDay; }
    public void setCigarettesPerDay(Integer cigarettesPerDay) { this.cigarettesPerDay = cigarettesPerDay; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public BigDecimal getPricePerPack() { return pricePerPack; }
    public void setPricePerPack(BigDecimal pricePerPack) { this.pricePerPack = pricePerPack; }

    public Integer getCigarettesPerPack() { return cigarettesPerPack; }
    public void setCigarettesPerPack(Integer cigarettesPerPack) { this.cigarettesPerPack = cigarettesPerPack; }

    public Integer getYearsOfSmoking() { return yearsOfSmoking; }
    public void setYearsOfSmoking(Integer yearsOfSmoking) { this.yearsOfSmoking = yearsOfSmoking; }

    public Integer getPreviousQuitAttempts() { return previousQuitAttempts; }
    public void setPreviousQuitAttempts(Integer previousQuitAttempts) { this.previousQuitAttempts = previousQuitAttempts; }

    public String getSmokingTriggers() { return smokingTriggers; }
    public void setSmokingTriggers(String smokingTriggers) { this.smokingTriggers = smokingTriggers; }

    public String getHealthConcerns() { return healthConcerns; }
    public void setHealthConcerns(String healthConcerns) { this.healthConcerns = healthConcerns; }

    public String getMotivations() { return motivations; }
    public void setMotivations(String motivations) { this.motivations = motivations; }

    public Double getCurrentWeight() { return currentWeight; }
    public void setCurrentWeight(Double currentWeight) { this.currentWeight = currentWeight; }

    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }

    public Integer getStressLevel() { return stressLevel; }
    public void setStressLevel(Integer stressLevel) { this.stressLevel = stressLevel; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
