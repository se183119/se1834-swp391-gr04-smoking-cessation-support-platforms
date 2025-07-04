package com.smokingcessation.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String excerpt; // tóm tắt

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "featured_image")
    private String featuredImage;

    @Column(name = "tags")
    private String tags; // phân cách bằng dấu phẩy

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    @Column(name = "likes_count")
    private Integer likesCount = 0;

    @Enumerated(EnumType.STRING)
    private BlogStatus status = BlogStatus.DRAFT;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BlogStatus {
        DRAFT, PUBLISHED, ARCHIVED
    }
}
