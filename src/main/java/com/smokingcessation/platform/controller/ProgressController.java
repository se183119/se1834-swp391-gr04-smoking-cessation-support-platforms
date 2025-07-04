package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.ProgressTracking;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.service.ProgressTrackingService;
import com.smokingcessation.platform.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Progress Tracking", description = "APIs for tracking daily progress, smoking events and health statistics")
public class ProgressController {

    private final ProgressTrackingService progressTrackingService;
    private final AchievementService achievementService;

    @Operation(summary = "Record daily progress",
               description = "Record comprehensive daily progress including mood, health status, exercise and notes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Progress recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid progress data")
    })
    @PostMapping("/daily/{userId}")
    public ResponseEntity<ProgressTracking> recordDailyProgress(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestBody DailyProgressRequest request) {
        try {
            ProgressTracking progress = new ProgressTracking();
            User user = new User();
            user.setId(userId);
            progress.setUser(user);
            progress.setTrackingDate(request.getTrackingDate());
            progress.setCigarettesSmoked(request.getCigarettesSmoked());
            progress.setMoodScore(request.getMoodScore());
            progress.setCravingIntensity(request.getCravingIntensity());
            progress.setExerciseMinutes(request.getExerciseMinutes());
            progress.setSleepHours(request.getSleepHours());
            progress.setHealthStatus(request.getHealthStatus());
            progress.setNotes(request.getNotes());

            ProgressTracking saved = progressTrackingService.recordDailyProgress(userId, progress);

            // Kiểm tra và trao huy hiệu sau khi ghi nhận tiến trình
            achievementService.checkAndAwardAchievements(userId);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Record smoking event",
               description = "Record a smoking event (number of cigarettes smoked)")
    @PostMapping("/smoking-event/{userId}")
    public ResponseEntity<ProgressTracking> recordSmokingEvent(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @RequestBody SmokingEventRequest request) {
        try {
            ProgressTracking updated = progressTrackingService.recordSmokingEvent(userId, request.getCigarettesSmoked());

            // Kiểm tra huy hiệu sau khi ghi nhận
            achievementService.checkAndAwardAchievements(userId);

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get user progress history",
               description = "Retrieve complete progress history for a user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProgressTracking>> getUserProgress(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<ProgressTracking> progress = progressTrackingService.getUserProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @Operation(summary = "Get today's progress",
               description = "Get progress tracking data for today")
    @GetMapping("/today/{userId}")
    public ResponseEntity<ProgressTracking> getTodayProgress(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        return progressTrackingService.getTodayProgress(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get progress in date range",
               description = "Retrieve progress data within specified date range")
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<ProgressTracking>> getUserProgressInRange(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Start date") @RequestParam LocalDate startDate,
            @Parameter(description = "End date") @RequestParam LocalDate endDate) {
        List<ProgressTracking> progress = progressTrackingService.getUserProgressInRange(userId, startDate, endDate);
        return ResponseEntity.ok(progress);
    }

    @Operation(summary = "Get user statistics",
               description = "Get comprehensive statistics including streak, money saved, total days tracked")
    @GetMapping("/stats/{userId}")
    public ResponseEntity<ProgressTrackingService.ProgressStats> getUserStats(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        ProgressTrackingService.ProgressStats stats = progressTrackingService.calculateUserStats(userId);
        return ResponseEntity.ok(stats);
    }

    // DTOs
    public static class DailyProgressRequest {
        private LocalDate trackingDate;
        private Integer cigarettesSmoked;
        private Integer moodScore;
        private Integer cravingIntensity;
        private Integer exerciseMinutes;
        private Double sleepHours;
        private ProgressTracking.HealthStatus healthStatus;
        private String notes;

        // Getters and setters
        public LocalDate getTrackingDate() { return trackingDate; }
        public void setTrackingDate(LocalDate trackingDate) { this.trackingDate = trackingDate; }
        public Integer getCigarettesSmoked() { return cigarettesSmoked; }
        public void setCigarettesSmoked(Integer cigarettesSmoked) { this.cigarettesSmoked = cigarettesSmoked; }
        public Integer getMoodScore() { return moodScore; }
        public void setMoodScore(Integer moodScore) { this.moodScore = moodScore; }
        public Integer getCravingIntensity() { return cravingIntensity; }
        public void setCravingIntensity(Integer cravingIntensity) { this.cravingIntensity = cravingIntensity; }
        public Integer getExerciseMinutes() { return exerciseMinutes; }
        public void setExerciseMinutes(Integer exerciseMinutes) { this.exerciseMinutes = exerciseMinutes; }
        public Double getSleepHours() { return sleepHours; }
        public void setSleepHours(Double sleepHours) { this.sleepHours = sleepHours; }
        public ProgressTracking.HealthStatus getHealthStatus() { return healthStatus; }
        public void setHealthStatus(ProgressTracking.HealthStatus healthStatus) { this.healthStatus = healthStatus; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class SmokingEventRequest {
        private Integer cigarettesSmoked;

        public Integer getCigarettesSmoked() { return cigarettesSmoked; }
        public void setCigarettesSmoked(Integer cigarettesSmoked) { this.cigarettesSmoked = cigarettesSmoked; }
    }
}
