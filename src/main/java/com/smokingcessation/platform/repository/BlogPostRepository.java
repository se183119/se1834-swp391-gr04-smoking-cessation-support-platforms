package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.BlogPost;
import com.smokingcessation.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    List<BlogPost> findByAuthor(User author);

    List<BlogPost> findByAuthorId(Long authorId);

    List<BlogPost> findByStatus(BlogPost.BlogStatus status);

    @Query("""
        SELECT bp
          FROM BlogPost bp
         WHERE bp.status = :status
         ORDER BY bp.publishedAt DESC
        """)
    List<BlogPost> findPublishedPostsOrderByPublishedAtDesc(@Param("status") BlogPost.BlogStatus status);

    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' ORDER BY bp.viewsCount DESC")
    List<BlogPost> findPopularPosts();

    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' AND bp.tags LIKE %:tag%")
    List<BlogPost> findByTag(@Param("tag") String tag);

    @Query("SELECT bp FROM BlogPost bp WHERE bp.status = 'PUBLISHED' AND (bp.title LIKE %:keyword% OR bp.content LIKE %:keyword%)")
    List<BlogPost> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT bp FROM BlogPost bp WHERE bp.author.id = :authorId ORDER BY bp.createdAt DESC")
    List<BlogPost> findByAuthorIdOrderByCreatedAtDesc(@Param("authorId") Long authorId);
}
