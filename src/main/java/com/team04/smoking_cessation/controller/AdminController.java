package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.SubscriptionPlan;
import com.team04.smoking_cessation.entity.Badge;
import com.team04.smoking_cessation.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Management", description = "Admin dashboard and system management APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard with system overview")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        Map<String, Object> dashboard = adminService.getAdminDashboard();
        return ResponseEntity.ok(dashboard);
    }

    // USER MANAGEMENT
    @GetMapping("/users")
    @Operation(summary = "Get all users with pagination")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = adminService.getAllUsers(pageable, search);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/users/{userId}/approve-coach")
    @Operation(summary = "Approve user as coach")
    public ResponseEntity<Map<String, String>> approveCoach(@PathVariable Long userId) {
        adminService.approveUserAsCoach(userId);
        return ResponseEntity.ok(Map.of("message", "User approved as coach successfully"));
    }

    @PostMapping("/users/{userId}/suspend")
    @Operation(summary = "Suspend user account")
    public ResponseEntity<Map<String, String>> suspendUser(@PathVariable Long userId, @RequestBody Map<String, String> reason) {
        adminService.suspendUser(userId, reason.get("reason"));
        return ResponseEntity.ok(Map.of("message", "User suspended successfully"));
    }

    @PostMapping("/users/{userId}/activate")
    @Operation(summary = "Activate suspended user")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable Long userId) {
        adminService.activateUser(userId);
        return ResponseEntity.ok(Map.of("message", "User activated successfully"));
    }

    // SUBSCRIPTION MANAGEMENT
    @GetMapping("/subscription-plans")
    @Operation(summary = "Get all subscription plans")
    public ResponseEntity<List<SubscriptionPlan>> getAllSubscriptionPlans() {
        List<SubscriptionPlan> plans = adminService.getAllSubscriptionPlans();
        return ResponseEntity.ok(plans);
    }

    @PostMapping("/subscription-plans")
    @Operation(summary = "Create new subscription plan")
    public ResponseEntity<SubscriptionPlan> createSubscriptionPlan(@RequestBody Map<String, Object> planData) {
        SubscriptionPlan plan = adminService.createSubscriptionPlan(planData);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/subscription-plans/{planId}")
    @Operation(summary = "Update subscription plan")
    public ResponseEntity<SubscriptionPlan> updateSubscriptionPlan(@PathVariable Long planId, @RequestBody Map<String, Object> planData) {
        SubscriptionPlan plan = adminService.updateSubscriptionPlan(planId, planData);
        return ResponseEntity.ok(plan);
    }

    // BADGE MANAGEMENT
    @GetMapping("/badges")
    @Operation(summary = "Get all badges")
    public ResponseEntity<List<Badge>> getAllBadges() {
        List<Badge> badges = adminService.getAllBadges();
        return ResponseEntity.ok(badges);
    }

    @PostMapping("/badges")
    @Operation(summary = "Create new badge")
    public ResponseEntity<Badge> createBadge(@RequestBody Map<String, Object> badgeData) {
        Badge badge = adminService.createBadge(badgeData);
        return ResponseEntity.ok(badge);
    }

    @PutMapping("/badges/{badgeId}")
    @Operation(summary = "Update badge")
    public ResponseEntity<Badge> updateBadge(@PathVariable Long badgeId, @RequestBody Map<String, Object> badgeData) {
        Badge badge = adminService.updateBadge(badgeId, badgeData);
        return ResponseEntity.ok(badge);
    }

    @DeleteMapping("/badges/{badgeId}")
    @Operation(summary = "Delete badge")
    public ResponseEntity<Map<String, String>> deleteBadge(@PathVariable Long badgeId) {
        adminService.deleteBadge(badgeId);
        return ResponseEntity.ok(Map.of("message", "Badge deleted successfully"));
    }

    // CONTENT MODERATION
    @GetMapping("/forum/reported-posts")
    @Operation(summary = "Get reported forum posts")
    public ResponseEntity<List<Map<String, Object>>> getReportedPosts() {
        List<Map<String, Object>> reportedPosts = adminService.getReportedPosts();
        return ResponseEntity.ok(reportedPosts);
    }

    @PostMapping("/forum/posts/{postId}/moderate")
    @Operation(summary = "Moderate forum post")
    public ResponseEntity<Map<String, String>> moderatePost(@PathVariable Long postId, @RequestBody Map<String, String> action) {
        adminService.moderatePost(postId, action.get("action"), action.get("reason"));
        return ResponseEntity.ok(Map.of("message", "Post moderated successfully"));
    }

    // ANALYTICS & REPORTS
    @GetMapping("/analytics/users")
    @Operation(summary = "Get user analytics")
    public ResponseEntity<Map<String, Object>> getUserAnalytics() {
        Map<String, Object> analytics = adminService.getUserAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/analytics/platform")
    @Operation(summary = "Get platform analytics")
    public ResponseEntity<Map<String, Object>> getPlatformAnalytics() {
        Map<String, Object> analytics = adminService.getPlatformAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/analytics/revenue")
    @Operation(summary = "Get revenue analytics")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics() {
        Map<String, Object> analytics = adminService.getRevenueAnalytics();
        return ResponseEntity.ok(analytics);
    }

    // SYSTEM CONFIGURATION
    @GetMapping("/config")
    @Operation(summary = "Get system configuration")
    public ResponseEntity<Map<String, Object>> getSystemConfig() {
        Map<String, Object> config = adminService.getSystemConfig();
        return ResponseEntity.ok(config);
    }

    @PutMapping("/config")
    @Operation(summary = "Update system configuration")
    public ResponseEntity<Map<String, String>> updateSystemConfig(@RequestBody Map<String, Object> config) {
        adminService.updateSystemConfig(config);
        return ResponseEntity.ok(Map.of("message", "System configuration updated successfully"));
    }

    // BACKUP & MAINTENANCE
    @PostMapping("/backup")
    @Operation(summary = "Create system backup")
    public ResponseEntity<Map<String, String>> createBackup() {
        String backupId = adminService.createSystemBackup();
        return ResponseEntity.ok(Map.of("message", "Backup created successfully", "backupId", backupId));
    }

    @GetMapping("/system-health")
    @Operation(summary = "Check system health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> health = adminService.getSystemHealth();
        return ResponseEntity.ok(health);
    }
}
