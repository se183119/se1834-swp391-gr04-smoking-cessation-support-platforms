package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.BlogPost;
import com.smokingcessation.platform.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BlogController {

    private final BlogService blogService;

    // Lấy tất cả bài blog đã xuất bản
    @GetMapping
    public ResponseEntity<List<BlogPost>> getAllPublishedPosts() {
        List<BlogPost> posts = blogService.getAllPublishedPosts();
        return ResponseEntity.ok(posts);
    }

    // Lấy bài blog phổ biến
    @GetMapping("/popular")
    public ResponseEntity<List<BlogPost>> getPopularPosts() {
        List<BlogPost> posts = blogService.getPopularPosts();
        return ResponseEntity.ok(posts);
    }

    // Lấy bài blog theo tác giả
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<BlogPost>> getPostsByAuthor(@PathVariable Long authorId) {
        List<BlogPost> posts = blogService.getPostsByAuthor(authorId);
        return ResponseEntity.ok(posts);
    }

    // Tìm kiếm bài blog
    @GetMapping("/search")
    public ResponseEntity<List<BlogPost>> searchPosts(@RequestParam String keyword) {
        List<BlogPost> posts = blogService.searchPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    // Lấy bài blog theo tag
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<BlogPost>> getPostsByTag(@PathVariable String tag) {
        List<BlogPost> posts = blogService.getPostsByTag(tag);
        return ResponseEntity.ok(posts);
    }

    // Lấy chi tiết bài blog
    @GetMapping("/{postId}")
    public ResponseEntity<BlogPost> getPostById(@PathVariable Long postId) {
        // Tăng lượt xem khi đọc bài
        blogService.incrementViewCount(postId);

        return blogService.getPostById(postId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Tạo bài blog mới
    @PostMapping
    public ResponseEntity<BlogPost> createPost(@RequestBody BlogPostRequest request) {
        try {
            BlogPost post = blogService.createPost(request.getAuthorId(), request.getTitle(),
                request.getContent(), request.getExcerpt(), request.getFeaturedImage(), request.getTags());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cập nhật bài blog
    @PutMapping("/{postId}")
    public ResponseEntity<BlogPost> updatePost(@PathVariable Long postId,
                                              @RequestBody BlogPostRequest request) {
        try {
            BlogPost post = blogService.updatePost(postId, request.getTitle(), request.getContent(),
                request.getExcerpt(), request.getFeaturedImage(), request.getTags());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xuất bản bài blog
    @PostMapping("/{postId}/publish")
    public ResponseEntity<BlogPost> publishPost(@PathVariable Long postId) {
        try {
            BlogPost post = blogService.publishPost(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Ẩn bài blog
    @PostMapping("/{postId}/archive")
    public ResponseEntity<BlogPost> archivePost(@PathVariable Long postId) {
        try {
            BlogPost post = blogService.archivePost(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Like bài blog
    @PostMapping("/{postId}/like")
    public ResponseEntity<BlogPost> likePost(@PathVariable Long postId) {
        try {
            BlogPost post = blogService.likePost(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Unlike bài blog
    @PostMapping("/{postId}/unlike")
    public ResponseEntity<BlogPost> unlikePost(@PathVariable Long postId) {
        try {
            BlogPost post = blogService.unlikePost(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xóa bài blog
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        try {
            blogService.deletePost(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTO
    public static class BlogPostRequest {
        private Long authorId;
        private String title;
        private String content;
        private String excerpt;
        private String featuredImage;
        private String tags;

        // Getters and setters
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getExcerpt() { return excerpt; }
        public void setExcerpt(String excerpt) { this.excerpt = excerpt; }
        public String getFeaturedImage() { return featuredImage; }
        public void setFeaturedImage(String featuredImage) { this.featuredImage = featuredImage; }
        public String getTags() { return tags; }
        public void setTags(String tags) { this.tags = tags; }
    }
}
