package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.DailyLog;
import com.team04.smoking_cessation.service.DailyLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/daily-logs")
@Tag(name = "Daily Logs", description = "Daily smoking tracking and progress APIs")
@SecurityRequirement(name = "bearerAuth")
public class DailyLogController {

    @Autowired
    private DailyLogService dailyLogService;

    @PostMapping
    @Operation(summary = "Create or update daily log")
    public ResponseEntity<DailyLog> createOrUpdateDailyLog(@RequestBody Map<String, Object> logData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        DailyLog dailyLog = dailyLogService.createOrUpdateDailyLog(email, logData);
        return ResponseEntity.ok(dailyLog);
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's log")
    public ResponseEntity<DailyLog> getTodayLog() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        DailyLog todayLog = dailyLogService.getTodayLog(email);
        return ResponseEntity.ok(todayLog);
    }

    @GetMapping("/history")
    @Operation(summary = "Get user's daily log history")
    public ResponseEntity<List<DailyLog>> getUserLogHistory(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<DailyLog> logs = dailyLogService.getUserLogHistory(email, startDate, endDate);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get smoking statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> stats = dailyLogService.getUserStatistics(email);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/streak")
    @Operation(summary = "Get current smoke-free streak")
    public ResponseEntity<Map<String, Object>> getCurrentStreak() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> streak = dailyLogService.getCurrentStreak(email);
        return ResponseEntity.ok(streak);
    }

    @GetMapping("/motivation")
    @Operation(summary = "Get motivational message")
    public ResponseEntity<Map<String, String>> getMotivationalMessage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String message = dailyLogService.generateMotivationalMessage(email);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/ai-analysis")
    @Operation(summary = "Get health analysis")
    public ResponseEntity<Map<String, Object>> getAIHealthAnalysis() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> analysis = dailyLogService.getAIHealthAnalysis(email);
        return ResponseEntity.ok(analysis);
    }
}
