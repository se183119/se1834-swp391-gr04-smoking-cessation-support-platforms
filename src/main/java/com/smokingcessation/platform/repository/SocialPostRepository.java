package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.SocialPost;
import com.smokingcessation.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialPostRepository extends JpaRepository<SocialPost, Long> {

    List<SocialPost> findByUser(User user);

    List<SocialPost> findByUserId(Long userId);

    List<SocialPost> findByType(SocialPost.PostType type);

    List<SocialPost> findByStatus(SocialPost.PostStatus status);

    @Query("SELECT sp FROM SocialPost sp WHERE sp.status = 'ACTIVE' ORDER BY sp.createdAt DESC")
    List<SocialPost> findActivePostsOrderByCreatedAtDesc();

    @Query("SELECT sp FROM SocialPost sp WHERE sp.type = 'ACHIEVEMENT_SHARE' AND sp.status = 'ACTIVE' ORDER BY sp.createdAt DESC")
    List<SocialPost> findAchievementSharePosts();

    @Query("SELECT sp FROM SocialPost sp WHERE sp.status = 'ACTIVE' ORDER BY sp.likesCount DESC")
    List<SocialPost> findPopularPosts();

    @Query("SELECT sp FROM SocialPost sp WHERE sp.user.id = :userId ORDER BY sp.createdAt DESC")
    List<SocialPost> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
