package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.ForumPost;
import com.team04.smoking_cessation.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    Page<ForumPost> findByParentPostIsNullAndStatusOrderByCreatedAtDesc(ForumPost.PostStatus status, Pageable pageable);

    Page<ForumPost> findByCategoryAndParentPostIsNullAndStatusOrderByCreatedAtDesc(
        ForumPost.PostCategory category, ForumPost.PostStatus status, Pageable pageable);

    List<ForumPost> findByParentPostOrderByCreatedAtAsc(ForumPost parentPost);

    List<ForumPost> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT fp FROM ForumPost fp WHERE fp.isPinned = true AND fp.status = :status ORDER BY fp.createdAt DESC")
    List<ForumPost> findPinnedPosts(@Param("status") ForumPost.PostStatus status);

    @Query("SELECT fp FROM ForumPost fp WHERE (fp.title LIKE %:searchTerm% OR fp.content LIKE %:searchTerm%) AND fp.status = :status")
    Page<ForumPost> searchPosts(@Param("searchTerm") String searchTerm, @Param("status") ForumPost.PostStatus status, Pageable pageable);

    @Query("SELECT COUNT(fp) FROM ForumPost fp WHERE fp.user = :user AND fp.parentPost IS NOT NULL")
    Long countRepliesByUser(@Param("user") User user);
}
