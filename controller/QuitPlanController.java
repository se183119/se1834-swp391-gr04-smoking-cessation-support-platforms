package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.QuitPlan;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.service.QuitPlanService;
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
@RequestMapping("/api/quit-plans")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Quit Plan Management", description = "APIs for creating and managing smoking cessation plans")
public class QuitPlanController {

    private final QuitPlanService quitPlanService;

    // Tạo kế hoạch cai thuốc thủ công
    @Operation(summary = "Create custom quit plan",
            description = "Create a manual quit plan with custom phases and timeline")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quit plan created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid quit plan data")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<QuitPlan> createQuitPlan(@Parameter(description = "User ID") @PathVariable Long userId,
                                                  @RequestBody QuitPlanRequest request) {
        try {
            User user = new User();
            user.setId(userId);

            QuitPlan quitPlan = new QuitPlan();
            quitPlan.setUser(user);
            quitPlan.setTitle(request.getTitle());
            quitPlan.setQuitReason(request.getQuitReason());
            quitPlan.setStartDate(request.getStartDate());
            quitPlan.setTargetQuitDate(request.getTargetQuitDate());
            quitPlan.setQuitMethod(request.getQuitMethod());
            quitPlan.setIsSystemGenerated(false);

            QuitPlan saved = quitPlanService.createQuitPlan(quitPlan);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Hệ thống tự động tạo kế hoạch cai thuốc
    @Operation(summary = "Generate system quit plan",
            description = "Auto-generate a scientific quit plan based on user's smoking status")
    @PostMapping("/{userId}/generate")
    public ResponseEntity<QuitPlan> generateSystemQuitPlan(@Parameter(description = "User ID") @PathVariable Long userId,
                                                          @RequestBody SystemQuitPlanRequest request) {
        try {
            User user = new User();
            user.setId(userId);

            QuitPlan quitPlan = quitPlanService.generateSystemQuitPlan(user,
                request.getQuitReason(), request.getStartDate(), request.getTargetQuitDate());
            return ResponseEntity.ok(quitPlan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy tất cả kế hoạch của user
    @Operation(summary = "Get user quit plans",
            description = "Retrieve all quit plans for a specific user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuitPlan>> getUserQuitPlans(@Parameter(description = "User ID") @PathVariable Long userId) {
        List<QuitPlan> plans = quitPlanService.findUserQuitPlans(userId);
        return ResponseEntity.ok(plans);
    }

    // Lấy kế hoạch đang hoạt động
    @Operation(summary = "Get active quit plan",
            description = "Get the currently active quit plan for a user")
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<QuitPlan> getActiveQuitPlan(@Parameter(description = "User ID") @PathVariable Long userId) {
        return quitPlanService.findActiveQuitPlan(userId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // Tùy chỉnh kế hoạch do hệ thống tạo
    @Operation(summary = "Customize quit plan",
            description = "Add custom notes and modifications to a system-generated plan")
    @PutMapping("/{planId}/customize")
    public ResponseEntity<QuitPlan> customizeQuitPlan(@Parameter(description = "Quit Plan ID") @PathVariable Long planId,
                                                     @RequestBody CustomizationRequest request) {
        try {
            QuitPlan customized = quitPlanService.customizeQuitPlan(planId, request.getCustomizationNotes());
            return ResponseEntity.ok(customized);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cập nhật trạng thái kế hoạch
    @PutMapping("/{planId}/status")
    public ResponseEntity<Void> updatePlanStatus(@PathVariable Long planId,
                                                @RequestBody StatusUpdateRequest request) {
        try {
            quitPlanService.updatePlanStatus(planId, request.getStatus());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy chi tiết kế hoạch
    @GetMapping("/{planId}")
    public ResponseEntity<QuitPlan> getQuitPlanDetails(@PathVariable Long planId) {
        return quitPlanService.findUserQuitPlans(planId) // This would need a findById method
            .stream()
            .findFirst()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // DTOs
    public static class QuitPlanRequest {
        private String title;
        private String quitReason;
        private LocalDate startDate;
        private LocalDate targetQuitDate;
        private QuitPlan.QuitMethod quitMethod;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getQuitReason() { return quitReason; }
        public void setQuitReason(String quitReason) { this.quitReason = quitReason; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getTargetQuitDate() { return targetQuitDate; }
        public void setTargetQuitDate(LocalDate targetQuitDate) { this.targetQuitDate = targetQuitDate; }
        public QuitPlan.QuitMethod getQuitMethod() { return quitMethod; }
        public void setQuitMethod(QuitPlan.QuitMethod quitMethod) { this.quitMethod = quitMethod; }
    }

    public static class SystemQuitPlanRequest {
        private String quitReason;
        private LocalDate startDate;
        private LocalDate targetQuitDate;

        // Getters and setters
        public String getQuitReason() { return quitReason; }
        public void setQuitReason(String quitReason) { this.quitReason = quitReason; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getTargetQuitDate() { return targetQuitDate; }
        public void setTargetQuitDate(LocalDate targetQuitDate) { this.targetQuitDate = targetQuitDate; }
    }

    public static class CustomizationRequest {
        private String customizationNotes;

        public String getCustomizationNotes() { return customizationNotes; }
        public void setCustomizationNotes(String customizationNotes) { this.customizationNotes = customizationNotes; }
    }

    public static class StatusUpdateRequest {
        private QuitPlan.PlanStatus status;

        public QuitPlan.PlanStatus getStatus() { return status; }
        public void setStatus(QuitPlan.PlanStatus status) { this.status = status; }
    }
}
