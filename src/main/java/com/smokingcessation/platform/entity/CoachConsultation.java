package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "coach_consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachConsultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @Column(nullable = false)
    private String subject;

    @Column(name = "member_message", columnDefinition = "TEXT")
    private String memberMessage;

    @Column(name = "coach_response", columnDefinition = "TEXT")
    private String coachResponse;

    @Enumerated(EnumType.STRING)
    private ConsultationType type;

    @Enumerated(EnumType.STRING)
    private ConsultationStatus status = ConsultationStatus.PENDING;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "rating")
    private Integer rating; // đánh giá 1-5 sao

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ConsultationType {
        CHAT, VIDEO_CALL, PHONE_CALL, EMAIL
    }

    public enum ConsultationStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, EXPIRED
    }
}
