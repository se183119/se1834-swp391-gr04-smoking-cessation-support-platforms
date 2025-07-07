package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.CoachFeedback;
import com.team04.smoking_cessation.service.CoachFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coach-feedbacks")
@Tag(name = "Coach Feedbacks", description = "APIs for submitting and viewing feedback after coach sessions")
@SecurityRequirement(name = "bearerAuth")
public class CoachFeedbackController {
    @Autowired
    private CoachFeedbackService coachFeedbackService;

    @PostMapping("/submit")
    @Operation(summary = "Submit feedback for a session (member)")
    public ResponseEntity<CoachFeedback> submitFeedback(@RequestBody Map<String, Object> req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = auth.getName();
        Long sessionId = Long.valueOf(req.get("sessionId").toString());
        int rating = Integer.parseInt(req.get("rating").toString());
        String content = req.getOrDefault("content", "").toString();
        CoachFeedback feedback = coachFeedbackService.submitFeedback(sessionId, memberEmail, rating, content);
        return ResponseEntity.ok(feedback);
    }

    @GetMapping("/session/{sessionId}")
    @Operation(summary = "Get all feedbacks for a session")
    public ResponseEntity<List<CoachFeedback>> getFeedbacksForSession(@PathVariable Long sessionId) {
        List<CoachFeedback> feedbacks = coachFeedbackService.getFeedbacksForSession(sessionId);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/coach/{coachId}")
    @Operation(summary = "Get all feedbacks for a coach")
    public ResponseEntity<List<CoachFeedback>> getFeedbacksForCoach(@PathVariable Long coachId) {
        List<CoachFeedback> feedbacks = coachFeedbackService.getFeedbacksForCoach(coachId);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get all feedbacks for a member")
    public ResponseEntity<List<CoachFeedback>> getFeedbacksForMember(@PathVariable Long memberId) {
        List<CoachFeedback> feedbacks = coachFeedbackService.getFeedbacksForMember(memberId);
        return ResponseEntity.ok(feedbacks);
    }
} 