package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.CoachSession;
import com.team04.smoking_cessation.service.CoachSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coach-sessions")
@Tag(name = "Coach Sessions", description = "APIs for booking and managing coach consultation sessions")
@SecurityRequirement(name = "bearerAuth")
public class CoachSessionController {
    @Autowired
    private CoachSessionService coachSessionService;

    @PostMapping("/book")
    @Operation(summary = "Book a new coach session (member)")
    public ResponseEntity<CoachSession> bookSession(@RequestBody Map<String, Object> req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = auth.getName();
        Long coachId = Long.valueOf(req.get("coachId").toString());
        LocalDateTime startTime = LocalDateTime.parse(req.get("startTime").toString());
        LocalDateTime endTime = LocalDateTime.parse(req.get("endTime").toString());
        String note = req.getOrDefault("note", "").toString();
        CoachSession session = coachSessionService.bookSession(memberEmail, coachId, startTime, endTime, note);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{sessionId}/confirm")
    @Operation(summary = "Coach confirm session")
    public ResponseEntity<CoachSession> confirmSession(@PathVariable Long sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String coachEmail = auth.getName();
        CoachSession session = coachSessionService.confirmSession(sessionId, coachEmail);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{sessionId}/cancel")
    @Operation(summary = "Cancel session (member or coach)")
    public ResponseEntity<CoachSession> cancelSession(@PathVariable Long sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();
        CoachSession session = coachSessionService.cancelSession(sessionId, userEmail);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/{sessionId}/complete")
    @Operation(summary = "Coach mark session as completed")
    public ResponseEntity<CoachSession> completeSession(@PathVariable Long sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String coachEmail = auth.getName();
        CoachSession session = coachSessionService.completeSession(sessionId, coachEmail);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/member")
    @Operation(summary = "Get all sessions for current member")
    public ResponseEntity<List<CoachSession>> getSessionsForMember() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String memberEmail = auth.getName();
        List<CoachSession> sessions = coachSessionService.getSessionsForMember(memberEmail);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/coach")
    @Operation(summary = "Get all sessions for current coach")
    public ResponseEntity<List<CoachSession>> getSessionsForCoach() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String coachEmail = auth.getName();
        List<CoachSession> sessions = coachSessionService.getSessionsForCoach(coachEmail);
        return ResponseEntity.ok(sessions);
    }
} 