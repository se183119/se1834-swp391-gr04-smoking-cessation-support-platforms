package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.QuitPlan;
import com.team04.smoking_cessation.service.QuitPlanService;
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
@RequestMapping("/quit-plans")
@Tag(name = "Quit Plans", description = "Quit smoking plan management APIs")
@SecurityRequirement(name = "bearerAuth")
public class QuitPlanController {

    @Autowired
    private QuitPlanService quitPlanService;

    @PostMapping
    @Operation(summary = "Create a new quit plan")
    public ResponseEntity<QuitPlan> createQuitPlan(@RequestBody Map<String, Object> planData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        QuitPlan quitPlan = quitPlanService.createQuitPlan(email, planData);
        return ResponseEntity.ok(quitPlan);
    }

    @PostMapping("/ai-generate")
    @Operation(summary = "Generate AI-powered quit plan")
    public ResponseEntity<QuitPlan> generateAiQuitPlan(@RequestBody Map<String, Object> userPreferences) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        QuitPlan aiPlan = quitPlanService.generateAiQuitPlan(email, userPreferences);
        return ResponseEntity.ok(aiPlan);
    }

    @GetMapping
    @Operation(summary = "Get all user's quit plans")
    public ResponseEntity<List<QuitPlan>> getUserQuitPlans() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<QuitPlan> plans = quitPlanService.getUserQuitPlans(email);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active quit plan")
    public ResponseEntity<QuitPlan> getActiveQuitPlan() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        QuitPlan activePlan = quitPlanService.getActiveQuitPlan(email);
        return ResponseEntity.ok(activePlan);
    }

    @GetMapping("/template")
    @Operation(summary = "Get template quit plan (non-personalized)")
    public ResponseEntity<QuitPlan> getTemplateQuitPlan() {
        QuitPlan template = quitPlanService.getTemplateQuitPlan();
        return ResponseEntity.ok(template);
    }

    @GetMapping("/{planId}")
    @Operation(summary = "Get quit plan by ID")
    public ResponseEntity<QuitPlan> getQuitPlan(@PathVariable Long planId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        QuitPlan plan = quitPlanService.getQuitPlanById(email, planId);
        return ResponseEntity.ok(plan);
    }

    @PutMapping("/{planId}")
    @Operation(summary = "Update quit plan")
    public ResponseEntity<QuitPlan> updateQuitPlan(@PathVariable Long planId, @RequestBody Map<String, Object> planData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        QuitPlan updatedPlan = quitPlanService.updateQuitPlan(email, planId, planData);
        return ResponseEntity.ok(updatedPlan);
    }

    @PostMapping("/{planId}/activate")
    @Operation(summary = "Activate a quit plan")
    public ResponseEntity<Map<String, String>> activateQuitPlan(@PathVariable Long planId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        quitPlanService.activateQuitPlan(email, planId);
        return ResponseEntity.ok(Map.of("message", "Quit plan activated successfully"));
    }

    @PostMapping("/{planId}/complete")
    @Operation(summary = "Mark quit plan as completed")
    public ResponseEntity<Map<String, String>> completeQuitPlan(@PathVariable Long planId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        quitPlanService.completeQuitPlan(email, planId);
        return ResponseEntity.ok(Map.of("message", "Congratulations! Quit plan completed successfully"));
    }

    @DeleteMapping("/{planId}")
    @Operation(summary = "Delete quit plan")
    public ResponseEntity<Map<String, String>> deleteQuitPlan(@PathVariable Long planId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        quitPlanService.deleteQuitPlan(email, planId);
        return ResponseEntity.ok(Map.of("message", "Quit plan deleted successfully"));
    }

    @GetMapping("/coping-strategies")
    @Operation(summary = "Get coping strategies")
    public ResponseEntity<Map<String, String>> getCopingStrategies(@RequestParam String quitMethod) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String strategies = quitPlanService.generateCopingStrategies(email, quitMethod);
        return ResponseEntity.ok(Map.of("strategies", strategies));
    }
}
