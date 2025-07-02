package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.SocialPost;
import com.smokingcessation.platform.service.SocialPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social-posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SocialPostController {

    private final SocialPostService socialPostService;

    // Lấy tất cả bài đăng đang hoạt động
    @GetMapping
    public ResponseEntity<List<SocialPost>> getAllActivePosts() {
        List<SocialPost> posts = socialPostService.getAllActivePosts();
        return ResponseEntity.ok(posts);
    }

    // Lấy bài đăng phổ biến
    @GetMapping("/popular")
    public ResponseEntity<List<SocialPost>> getPopularPosts() {
        List<SocialPost> posts = socialPostService.getPopularPosts();
        return ResponseEntity.ok(posts);
    }

    // Lấy bài đăng chia sẻ huy hiệu
    @GetMapping("/achievements")
    public ResponseEntity<List<SocialPost>> getAchievementSharePosts() {
        List<SocialPost> posts = socialPostService.getAchievementSharePosts();
        return ResponseEntity.ok(posts);
    }

    // Lấy bài đăng của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SocialPost>> getUserPosts(@PathVariable Long userId) {
        List<SocialPost> posts = socialPostService.getUserPosts(userId);
        return ResponseEntity.ok(posts);
    }

    // Tạo bài đăng mới
    @PostMapping("/{userId}")
    public ResponseEntity<SocialPost> createPost(@PathVariable Long userId,
                                                @RequestBody PostRequest request) {
        try {
            SocialPost post = socialPostService.createPost(userId, request.getContent(), request.getType());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Chia sẻ huy hiệu thành tích
    @PostMapping("/{userId}/share-achievement")
    public ResponseEntity<SocialPost> shareAchievement(@PathVariable Long userId,
                                                      @RequestBody ShareAchievementRequest request) {
        try {
            SocialPost post = socialPostService.shareAchievement(userId,
                request.getUserAchievementId(), request.getMessage());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Đăng bài động viên
    @PostMapping("/{userId}/motivation")
    public ResponseEntity<SocialPost> createMotivationPost(@PathVariable Long userId,
                                                          @RequestBody ContentRequest request) {
        try {
            SocialPost post = socialPostService.createMotivationPost(userId, request.getContent());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Chia sẻ kinh nghiệm
    @PostMapping("/{userId}/experience")
    public ResponseEntity<SocialPost> shareExperience(@PathVariable Long userId,
                                                     @RequestBody ContentRequest request) {
        try {
            SocialPost post = socialPostService.shareExperience(userId, request.getContent());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Đặt câu hỏi
    @PostMapping("/{userId}/question")
    public ResponseEntity<SocialPost> askQuestion(@PathVariable Long userId,
                                                 @RequestBody ContentRequest request) {
        try {
            SocialPost post = socialPostService.askQuestion(userId, request.getContent());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Đưa ra lời khuyên
    @PostMapping("/{userId}/advice")
    public ResponseEntity<SocialPost> giveAdvice(@PathVariable Long userId,
                                                @RequestBody ContentRequest request) {
        try {
            SocialPost post = socialPostService.giveAdvice(userId, request.getContent());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Like bài đăng
    @PostMapping("/{postId}/like")
    public ResponseEntity<SocialPost> likePost(@PathVariable Long postId) {
        try {
            SocialPost post = socialPostService.likePost(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Unlike bài đăng
    @PostMapping("/{postId}/unlike")
    public ResponseEntity<SocialPost> unlikePost(@PathVariable Long postId) {
        try {
            SocialPost post = socialPostService.unlikePost(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cập nhật bài đăng
    @PutMapping("/{postId}")
    public ResponseEntity<SocialPost> updatePost(@PathVariable Long postId,
                                                @RequestBody ContentRequest request) {
        try {
            SocialPost post = socialPostService.updatePost(postId, request.getContent());
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Pin bài đăng
    @PostMapping("/{postId}/pin")
    public ResponseEntity<Void> pinPost(@PathVariable Long postId) {
        try {
            socialPostService.pinPost(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Unpin bài đăng
    @PostMapping("/{postId}/unpin")
    public ResponseEntity<Void> unpinPost(@PathVariable Long postId) {
        try {
            socialPostService.unpinPost(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Báo cáo bài đăng
    @PostMapping("/{postId}/report")
    public ResponseEntity<Void> reportPost(@PathVariable Long postId) {
        try {
            socialPostService.reportPost(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Xóa bài đăng
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        try {
            socialPostService.deletePost(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTOs
    public static class PostRequest {
        private String content;
        private SocialPost.PostType type;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public SocialPost.PostType getType() { return type; }
        public void setType(SocialPost.PostType type) { this.type = type; }
    }

    public static class ShareAchievementRequest {
        private Long userAchievementId;
        private String message;

        public Long getUserAchievementId() { return userAchievementId; }
        public void setUserAchievementId(Long userAchievementId) { this.userAchievementId = userAchievementId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ContentRequest {
        private String content;

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}
