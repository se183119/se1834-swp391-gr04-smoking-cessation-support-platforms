package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.ForumPost;
import com.team04.smoking_cessation.service.ForumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/forum")
@Tag(name = "Community Forum", description = "Community discussion and support APIs")
@SecurityRequirement(name = "bearerAuth")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @GetMapping("/posts")
    @Operation(summary = "Get forum posts with pagination")
    public ResponseEntity<Page<ForumPost>> getForumPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ForumPost.PostCategory category) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ForumPost> posts = forumService.getForumPosts(pageable, category);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/posts")
    @Operation(summary = "Create a new forum post")
    public ResponseEntity<ForumPost> createPost(@RequestBody Map<String, Object> postData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ForumPost post = forumService.createPost(email, postData);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts/{postId}")
    @Operation(summary = "Get forum post with replies")
    public ResponseEntity<Map<String, Object>> getPostWithReplies(@PathVariable Long postId) {
        Map<String, Object> postWithReplies = forumService.getPostWithReplies(postId);
        return ResponseEntity.ok(postWithReplies);
    }

    @PostMapping("/posts/{postId}/reply")
    @Operation(summary = "Reply to a forum post")
    public ResponseEntity<ForumPost> replyToPost(@PathVariable Long postId, @RequestBody Map<String, Object> replyData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ForumPost reply = forumService.replyToPost(email, postId, replyData);
        return ResponseEntity.ok(reply);
    }

    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "Like/unlike a forum post")
    public ResponseEntity<Map<String, Object>> togglePostLike(@PathVariable Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> result = forumService.togglePostLike(email, postId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/posts/search")
    @Operation(summary = "Search forum posts")
    public ResponseEntity<Page<ForumPost>> searchPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ForumPost> posts = forumService.searchPosts(query, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/my-posts")
    @Operation(summary = "Get current user's posts")
    public ResponseEntity<List<ForumPost>> getMyPosts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<ForumPost> posts = forumService.getUserPosts(email);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/stats")
    @Operation(summary = "Get forum statistics")
    public ResponseEntity<Map<String, Object>> getForumStats() {
        Map<String, Object> stats = forumService.getForumStatistics();
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/posts/{postId}")
    @Operation(summary = "Delete forum post")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        forumService.deletePost(email, postId);
        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }
}
