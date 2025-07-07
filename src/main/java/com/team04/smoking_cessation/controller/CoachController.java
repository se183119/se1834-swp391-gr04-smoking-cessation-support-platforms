package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.ChatMessage;
import com.team04.smoking_cessation.service.CoachService;
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
@RequestMapping("/coach")
@Tag(name = "Coach Management", description = "Coach dashboard and client management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
public class CoachController {

    @Autowired
    private CoachService coachService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get coach dashboard")
    public ResponseEntity<Map<String, Object>> getCoachDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> dashboard = coachService.getCoachDashboard(email);
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/clients")
    @Operation(summary = "Get assigned clients")
    public ResponseEntity<List<User>> getAssignedClients() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<User> clients = coachService.getAssignedClients(email);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/clients/{clientId}/profile")
    @Operation(summary = "Get client profile and progress")
    public ResponseEntity<Map<String, Object>> getClientProfile(@PathVariable Long clientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> profile = coachService.getClientProfile(email, clientId);
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/sessions")
    @Operation(summary = "Create coaching session")
    public ResponseEntity<Map<String, Object>> createCoachingSession(@RequestBody Map<String, Object> sessionData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> session = coachService.createCoachingSession(email, sessionData);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/sessions")
    @Operation(summary = "Get coaching sessions")
    public ResponseEntity<List<Map<String, Object>>> getCoachingSessions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<Map<String, Object>> sessions = coachService.getCoachingSessions(email);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/clients/{clientId}/message")
    @Operation(summary = "Send message to client")
    public ResponseEntity<ChatMessage> sendMessageToClient(
            @PathVariable Long clientId,
            @RequestBody Map<String, String> messageData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ChatMessage message = coachService.sendMessageToClient(email, clientId, messageData.get("content"));
        return ResponseEntity.ok(message);
    }

    @GetMapping("/clients/{clientId}/messages")
    @Operation(summary = "Get chat history with client")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable Long clientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<ChatMessage> messages = coachService.getChatHistory(email, clientId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/clients/{clientId}/notes")
    @Operation(summary = "Add coaching notes for client")
    public ResponseEntity<Map<String, String>> addCoachingNotes(
            @PathVariable Long clientId,
            @RequestBody Map<String, String> notesData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        coachService.addCoachingNotes(email, clientId, notesData.get("notes"));
        return ResponseEntity.ok(Map.of("message", "Notes added successfully"));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get coach performance statistics")
    public ResponseEntity<Map<String, Object>> getCoachStatistics() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> stats = coachService.getCoachStatistics(email);
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/availability")
    @Operation(summary = "Set coach availability")
    public ResponseEntity<Map<String, String>> setAvailability(@RequestBody Map<String, Object> availabilityData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        coachService.setCoachAvailability(email, availabilityData);
        return ResponseEntity.ok(Map.of("message", "Availability updated successfully"));
    }
}
