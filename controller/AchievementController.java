package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.Achievement;
import com.smokingcessation.platform.entity.UserAchievement;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Achievement System", description = "APIs for managing achievements, badges and user rewards")
public class AchievementController {

    private final AchievementService achievementService;

    // Lấy tất cả huy hiệu có trong hệ thống
    @Operation(summary = "Get all achievements", description = "Retrieve all available achievements in the system")
    @GetMapping
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        List<Achievement> achievements = achievementService.getAllAchievements();
        return ResponseEntity.ok(achievements);
    }

    // Lấy huy hiệu theo loại
    @Operation(summary = "Get achievements by type", description = "Filter achievements by specific type")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Achievement>> getAchievementsByType(@Parameter(description = "Achievement type") @PathVariable Achievement.AchievementType type) {
        List<Achievement> achievements = achievementService.getAchievementsByType(type);
        return ResponseEntity.ok(achievements);
    }

    // Lấy danh sách huy hiệu của user
    @Operation(summary = "Get user achievements", description = "Get all achievements earned by a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAchievement>> getUserAchievements(@Parameter(description = "User ID") @PathVariable Long userId) {
        List<UserAchievement> userAchievements = achievementService.getUserAchievements(userId);
        return ResponseEntity.ok(userAchievements);
    }

    // Lấy số lượng huy hiệu của user
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getUserAchievementCount(@PathVariable Long userId) {
        Long count = achievementService.getUserAchievementCount(userId);
        return ResponseEntity.ok(count);
    }

    // Lấy danh sách huy hiệu đã được chia sẻ
    @GetMapping("/shared")
    public ResponseEntity<List<UserAchievement>> getSharedAchievements() {
        List<UserAchievement> sharedAchievements = achievementService.getSharedAchievements();
        return ResponseEntity.ok(sharedAchievements);
    }

    // Chia sẻ huy hiệu lên mạng xã hội
    @Operation(summary = "Share achievement", description = "Share an achievement to social media")
    @PostMapping("/share/{userAchievementId}")
    public ResponseEntity<UserAchievement> shareAchievement(@Parameter(description = "User Achievement ID") @PathVariable Long userAchievementId) {
        try {
            UserAchievement shared = achievementService.shareAchievement(userAchievementId);
            return ResponseEntity.ok(shared);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Kiểm tra và trao huy hiệu thủ công (cho admin hoặc debug)
    @PostMapping("/check/{userId}")
    public ResponseEntity<Void> checkAndAwardAchievements(@PathVariable Long userId) {
        try {
            achievementService.checkAndAwardAchievements(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: Tạo huy hiệu mới
    @Operation(summary = "[ADMIN] Create achievement", description = "Admin function to create new achievement")
    @PostMapping
    public ResponseEntity<Achievement> createAchievement(@RequestBody AchievementRequest request) {
        try {
            Achievement achievement = new Achievement();
            achievement.setName(request.getName());
            achievement.setDescription(request.getDescription());
            achievement.setBadgeIcon(request.getBadgeIcon());
            achievement.setType(request.getType());
            achievement.setTargetValue(request.getTargetValue());
            achievement.setTargetMoney(request.getTargetMoney());
            achievement.setLevel(request.getLevel());
            achievement.setPointsAwarded(request.getPointsAwarded());
            achievement.setIsShareable(request.getIsShareable());

            Achievement created = achievementService.createAchievement(achievement);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: Cập nhật huy hiệu
    @PutMapping("/{achievementId}")
    public ResponseEntity<Achievement> updateAchievement(@PathVariable Long achievementId,
                                                        @RequestBody AchievementRequest request) {
        try {
            Achievement achievement = new Achievement();
            achievement.setId(achievementId);
            achievement.setName(request.getName());
            achievement.setDescription(request.getDescription());
            achievement.setBadgeIcon(request.getBadgeIcon());
            achievement.setType(request.getType());
            achievement.setTargetValue(request.getTargetValue());
            achievement.setTargetMoney(request.getTargetMoney());
            achievement.setLevel(request.getLevel());
            achievement.setPointsAwarded(request.getPointsAwarded());
            achievement.setIsShareable(request.getIsShareable());

            Achievement updated = achievementService.updateAchievement(achievement);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Admin: Xóa huy hiệu
    @DeleteMapping("/{achievementId}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Long achievementId) {
        try {
            achievementService.deleteAchievement(achievementId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTO
    public static class AchievementRequest {
        private String name;
        private String description;
        private String badgeIcon;
        private Achievement.AchievementType type;
        private Integer targetValue;
        private BigDecimal targetMoney;
        private Achievement.AchievementLevel level;
        private Integer pointsAwarded;
        private Boolean isShareable;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getBadgeIcon() { return badgeIcon; }
        public void setBadgeIcon(String badgeIcon) { this.badgeIcon = badgeIcon; }
        public Achievement.AchievementType getType() { return type; }
        public void setType(Achievement.AchievementType type) { this.type = type; }
        public Integer getTargetValue() { return targetValue; }
        public void setTargetValue(Integer targetValue) { this.targetValue = targetValue; }
        public BigDecimal getTargetMoney() { return targetMoney; }
        public void setTargetMoney(BigDecimal targetMoney) { this.targetMoney = targetMoney; }
        public Achievement.AchievementLevel getLevel() { return level; }
        public void setLevel(Achievement.AchievementLevel level) { this.level = level; }
        public Integer getPointsAwarded() { return pointsAwarded; }
        public void setPointsAwarded(Integer pointsAwarded) { this.pointsAwarded = pointsAwarded; }
        public Boolean getIsShareable() { return isShareable; }
        public void setIsShareable(Boolean isShareable) { this.isShareable = isShareable; }
    }
}
