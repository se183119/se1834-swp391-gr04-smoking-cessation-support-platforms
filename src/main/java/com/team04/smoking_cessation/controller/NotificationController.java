package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Management", description = "APIs for managing notifications and motivational messages")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send-custom")
    @Operation(summary = "Send custom notification to specific user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COACH')")
    public ResponseEntity<Map<String, String>> sendCustomNotification(
            @RequestParam String userEmail,
            @RequestParam String subject,
            @RequestParam String message) {
        
        notificationService.sendCustomNotification(userEmail, subject, message);
        return ResponseEntity.ok(Map.of("message", "Notification sent successfully"));
    }

    @PostMapping("/send-premium")
    @Operation(summary = "Send notification to all premium users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> sendNotificationToPremiumUsers(
            @RequestParam String subject,
            @RequestParam String message) {
        
        notificationService.sendNotificationToPremiumUsers(subject, message);
        return ResponseEntity.ok(Map.of("message", "Notification sent to premium users"));
    }

    @PostMapping("/test-daily")
    @Operation(summary = "Test daily motivational notifications (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> testDailyNotifications() {
        notificationService.sendDailyMotivationalNotifications();
        return ResponseEntity.ok(Map.of("message", "Daily notifications test completed"));
    }

    @PostMapping("/test-weekly")
    @Operation(summary = "Test weekly progress notifications (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> testWeeklyNotifications() {
        notificationService.sendWeeklyProgressNotifications();
        return ResponseEntity.ok(Map.of("message", "Weekly notifications test completed"));
    }

    @PostMapping("/test-milestone")
    @Operation(summary = "Test milestone notifications (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> testMilestoneNotifications() {
        notificationService.sendMilestoneNotifications();
        return ResponseEntity.ok(Map.of("message", "Milestone notifications test completed"));
    }

    @PostMapping("/test-log-reminder")
    @Operation(summary = "Test daily log reminder notifications (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> testLogReminderNotifications() {
        notificationService.sendDailyLogReminders();
        return ResponseEntity.ok(Map.of("message", "Log reminder notifications test completed"));
    }
} 