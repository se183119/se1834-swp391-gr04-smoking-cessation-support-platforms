package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.Achievement;
import com.team04.smoking_cessation.entity.Badge;
import com.team04.smoking_cessation.service.AchievementService;
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
@RequestMapping("/achievements")
@Tag(name = "Achievements", description = "Achievement and badge system APIs")
@SecurityRequirement(name = "bearerAuth")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @GetMapping
    @Operation(summary = "Get user's achievements")
    public ResponseEntity<List<Achievement>> getUserAchievements() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<Achievement> achievements = achievementService.getUserAchievements(email);
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("/badges")
    @Operation(summary = "Get all available badges")
    public ResponseEntity<List<Badge>> getAllBadges() {
        List<Badge> badges = achievementService.getAllAvailableBadges();
        return ResponseEntity.ok(badges);
    }

    @GetMapping("/progress")
    @Operation(summary = "Get achievement progress")
    public ResponseEntity<Map<String, Object>> getAchievementProgress() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> progress = achievementService.getAchievementProgress(email);
        return ResponseEntity.ok(progress);
    }

    @PostMapping("/check")
    @Operation(summary = "Check and award new achievements")
    public ResponseEntity<List<Achievement>> checkNewAchievements() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<Achievement> newAchievements = achievementService.checkAndAwardAchievements(email);
        return ResponseEntity.ok(newAchievements);
    }

    @PostMapping("/{achievementId}/share")
    @Operation(summary = "Share achievement on community")
    public ResponseEntity<Map<String, String>> shareAchievement(@PathVariable Long achievementId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        achievementService.shareAchievement(email, achievementId);
        return ResponseEntity.ok(Map.of("message", "Achievement shared successfully"));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get achievements leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard() {
        List<Map<String, Object>> leaderboard = achievementService.getAchievementLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }
}
