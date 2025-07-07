package com.team04.smoking_cessation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
    private String iconUrl;
    private String color;

    @Enumerated(EnumType.STRING)
    private BadgeType type;

    @Enumerated(EnumType.STRING)
    private BadgeCategory category;

    private Integer requiredValue; // days, money amount, etc.
    private String criteria; // JSON string of criteria

    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "badge", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Achievement> achievements;

    // Constructors
    public Badge() {}

    public Badge(String name, String description, BadgeType type, BadgeCategory category, Integer requiredValue) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.category = category;
        this.requiredValue = requiredValue;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public BadgeType getType() { return type; }
    public void setType(BadgeType type) { this.type = type; }

    public BadgeCategory getCategory() { return category; }
    public void setCategory(BadgeCategory category) { this.category = category; }

    public Integer getRequiredValue() { return requiredValue; }
    public void setRequiredValue(Integer requiredValue) { this.requiredValue = requiredValue; }

    public String getCriteria() { return criteria; }
    public void setCriteria(String criteria) { this.criteria = criteria; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Achievement> getAchievements() { return achievements; }
    public void setAchievements(List<Achievement> achievements) { this.achievements = achievements; }

    // Enums
    public enum BadgeType {
        TIME_BASED, MONEY_BASED, HEALTH_BASED, SOCIAL_BASED, MILESTONE
    }

    public enum BadgeCategory {
        SMOKE_FREE_DAYS, MONEY_SAVED, HEALTH_IMPROVEMENT, COMMUNITY_SUPPORT, SPECIAL_EVENT
    }
}
