package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private PostType type;

    @ManyToOne
    @JoinColumn(name = "shared_achievement_id")
    private UserAchievement sharedAchievement; // huy hiệu được chia sẻ

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    private Integer commentsCount = 0;

    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PostType {
        ACHIEVEMENT_SHARE, // chia sẻ huy hiệu
        MOTIVATION, // động viên
        EXPERIENCE_SHARE, // chia sẻ kinh nghiệm
        QUESTION, // đặt câu hỏi
        ADVICE // lời khuyên
    }

    public enum PostStatus {
        ACTIVE, HIDDEN, REPORTED, DELETED
    }
}
