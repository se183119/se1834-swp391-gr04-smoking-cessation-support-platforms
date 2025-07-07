package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.GeneralFeedback;
import com.team04.smoking_cessation.service.GeneralFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/general-feedbacks")
@Tag(name = "General Feedbacks", description = "APIs for feedback/rating on posts, services, etc.")
@SecurityRequirement(name = "bearerAuth")
public class GeneralFeedbackController {
    @Autowired
    private GeneralFeedbackService feedbackService;

    @PostMapping("/submit")
    @Operation(summary = "Submit feedback for post/service (member)")
    public ResponseEntity<GeneralFeedback> submitFeedback(@RequestBody Map<String, Object> req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        GeneralFeedback.FeedbackType type = GeneralFeedback.FeedbackType.valueOf(req.get("type").toString());
        Long targetId = Long.valueOf(req.get("targetId").toString());
        int rating = Integer.parseInt(req.get("rating").toString());
        String content = req.getOrDefault("content", "").toString();
        GeneralFeedback feedback = feedbackService.submitFeedback(userEmail, type, targetId, rating, content);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get all feedbacks by type (POST/SERVICE)")
    public ResponseEntity<List<GeneralFeedback>> getFeedbacksByType(@PathVariable String type) {
        List<GeneralFeedback> feedbacks = feedbackService.getFeedbacksByType(GeneralFeedback.FeedbackType.valueOf(type));
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/target/{type}/{targetId}")
    @Operation(summary = "Get all feedbacks for a target (e.g. post/service)")
    public ResponseEntity<List<GeneralFeedback>> getFeedbacksForTarget(@PathVariable String type, @PathVariable Long targetId) {
        List<GeneralFeedback> feedbacks = feedbackService.getFeedbacksForTarget(targetId, GeneralFeedback.FeedbackType.valueOf(type));
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/my-feedbacks")
    @Operation(summary = "Get all feedbacks by current user")
    public ResponseEntity<List<GeneralFeedback>> getFeedbacksByUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        List<GeneralFeedback> feedbacks = feedbackService.getFeedbacksByUser(userEmail);
        return ResponseEntity.ok(feedbacks);
    }

    // ADMIN APIs
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all feedbacks by status (PENDING/APPROVED/REJECTED) (admin)")
    public ResponseEntity<List<GeneralFeedback>> getFeedbacksByStatus(@PathVariable String status) {
        List<GeneralFeedback> feedbacks = feedbackService.getFeedbacksByStatus(GeneralFeedback.FeedbackStatus.valueOf(status));
        return ResponseEntity.ok(feedbacks);
    }

    @PostMapping("/{feedbackId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve feedback (admin)")
    public ResponseEntity<GeneralFeedback> approveFeedback(@PathVariable Long feedbackId) {
        GeneralFeedback feedback = feedbackService.approveFeedback(feedbackId);
        return ResponseEntity.ok(feedback);
    }

    @PostMapping("/{feedbackId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject feedback (admin)")
    public ResponseEntity<GeneralFeedback> rejectFeedback(@PathVariable Long feedbackId) {
        GeneralFeedback feedback = feedbackService.rejectFeedback(feedbackId);
        return ResponseEntity.ok(feedback);
    }
} 