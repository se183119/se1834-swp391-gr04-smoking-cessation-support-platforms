package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.service.PublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
@Tag(name = "Public APIs", description = "Public endpoints for guest users")
public class PublicController {

    @Autowired
    private PublicService publicService;

    @GetMapping("/platform-info")
    @Operation(summary = "Get platform information for homepage")
    public ResponseEntity<Map<String, Object>> getPlatformInfo() {
        Map<String, Object> info = publicService.getPlatformInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get public leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getPublicLeaderboard() {
        List<Map<String, Object>> leaderboard = publicService.getPublicLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/success-stories")
    @Operation(summary = "Get success stories for homepage")
    public ResponseEntity<List<Map<String, Object>>> getSuccessStories() {
        List<Map<String, Object>> stories = publicService.getSuccessStories();
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/subscription-plans")
    @Operation(summary = "Get available subscription plans")
    public ResponseEntity<List<Map<String, Object>>> getSubscriptionPlans() {
        List<Map<String, Object>> plans = publicService.getSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/platform-statistics")
    @Operation(summary = "Get platform statistics for homepage")
    public ResponseEntity<Map<String, Object>> getPlatformStatistics() {
        Map<String, Object> stats = publicService.getPlatformStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/blog-posts")
    @Operation(summary = "Get blog posts about smoking cessation")
    public ResponseEntity<List<Map<String, Object>>> getBlogPosts() {
        List<Map<String, Object>> posts = publicService.getBlogPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/testimonials")
    @Operation(summary = "Get user testimonials")
    public ResponseEntity<List<Map<String, Object>>> getTestimonials() {
        List<Map<String, Object>> testimonials = publicService.getTestimonials();
        return ResponseEntity.ok(testimonials);
    }

    @GetMapping("/coaches")
    @Operation(summary = "Get available coaches information")
    public ResponseEntity<List<Map<String, Object>>> getAvailableCoaches() {
        List<Map<String, Object>> coaches = publicService.getAvailableCoaches();
        return ResponseEntity.ok(coaches);
    }

    @PostMapping("/contact")
    @Operation(summary = "Send contact message")
    public ResponseEntity<Map<String, String>> sendContactMessage(@RequestBody Map<String, String> contactData) {
        publicService.sendContactMessage(contactData);
        return ResponseEntity.ok(Map.of("message", "Contact message sent successfully"));
    }

    @GetMapping("/faq")
    @Operation(summary = "Get frequently asked questions")
    public ResponseEntity<List<Map<String, Object>>> getFAQ() {
        List<Map<String, Object>> faq = publicService.getFAQ();
        return ResponseEntity.ok(faq);
    }
}
