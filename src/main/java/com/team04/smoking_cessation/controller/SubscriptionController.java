package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.SubscriptionPlan;
import com.team04.smoking_cessation.entity.UserSubscription;
import com.team04.smoking_cessation.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscription Management", description = "APIs for managing user subscriptions and payments")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/plans")
    @Operation(summary = "Get available subscription plans")
    public ResponseEntity<List<SubscriptionPlan>> getAvailablePlans() {
        List<SubscriptionPlan> plans = subscriptionService.getAvailablePlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/plans/{planId}")
    @Operation(summary = "Get subscription plan by ID")
    public ResponseEntity<SubscriptionPlan> getPlanById(@PathVariable Long planId) {
        SubscriptionPlan plan = subscriptionService.getPlanById(planId);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/current")
    @Operation(summary = "Get current user subscription")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> getCurrentSubscription(@RequestParam String userEmail) {
        Optional<UserSubscription> subscription = subscriptionService.getCurrentSubscription(userEmail);
        
        if (subscription.isPresent()) {
            UserSubscription sub = subscription.get();
            Map<String, Object> response = Map.of(
                "subscriptionId", sub.getId(),
                "planName", sub.getPlan().getName(),
                "status", sub.getStatus(),
                "billingCycle", sub.getBillingCycle(),
                "amount", sub.getAmount(),
                "startDate", sub.getStartDate(),
                "endDate", sub.getEndDate(),
                "nextBillingDate", sub.getNextBillingDate(),
                "autoRenew", sub.getAutoRenew()
            );
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(Map.of("status", "FREE", "planName", "FREE"));
        }
    }

    @PostMapping("/create-payment-link")
    @Operation(summary = "Create PayOS payment link for subscription")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> createPaymentLink(
            @RequestParam String userEmail,
            @RequestParam Long planId,
            @RequestParam String billingCycle,
            @RequestParam String returnUrl,
            @RequestParam String cancelUrl) {
        
        Map<String, Object> result = subscriptionService.createPaymentLink(userEmail, planId, billingCycle, returnUrl, cancelUrl);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel current subscription")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Map<String, String>> cancelSubscription(@RequestParam String userEmail) {
        subscriptionService.cancelSubscription(userEmail);
        return ResponseEntity.ok(Map.of("message", "Subscription cancelled successfully"));
    }

    @PutMapping("/update")
    @Operation(summary = "Update subscription (upgrade/downgrade)")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> updateSubscription(
            @RequestParam String userEmail,
            @RequestParam Long newPlanId) {
        
        Map<String, Object> result = subscriptionService.updateSubscription(userEmail, newPlanId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/billing")
    @Operation(summary = "Get billing information")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> getBillingInfo(@RequestParam String userEmail) {
        Map<String, Object> billingInfo = subscriptionService.getBillingInfo(userEmail);
        return ResponseEntity.ok(billingInfo);
    }

    @GetMapping("/check-access")
    @Operation(summary = "Check if user has access to specific feature")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> checkFeatureAccess(
            @RequestParam String userEmail,
            @RequestParam String feature) {
        
        boolean hasAccess = subscriptionService.hasFeatureAccess(userEmail, feature);
        return ResponseEntity.ok(Map.of(
            "feature", feature,
            "hasAccess", hasAccess
        ));
    }

    @PostMapping("/webhook")
    @Operation(summary = "Handle PayOS webhook events")
    public ResponseEntity<Map<String, String>> handleWebhook(
            @RequestBody Map<String, Object> payload,
            @RequestHeader("x-signature") String signature) {
        
        try {
            String orderCode = (String) payload.get("orderCode");
            String transactionId = (String) payload.get("transactionId");
            String status = (String) payload.get("status");
            
            subscriptionService.handlePayosWebhook(orderCode, transactionId, status);
            return ResponseEntity.ok(Map.of("status", "success"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/features")
    @Operation(summary = "Get available features for current subscription")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Map<String, Object>> getAvailableFeatures(@RequestParam String userEmail) {
        Map<String, Object> features = new HashMap<>();
        features.put("account_management", subscriptionService.hasFeatureAccess(userEmail, "account_management"));
        features.put("personal_info_management", subscriptionService.hasFeatureAccess(userEmail, "personal_info_management"));
        features.put("premium_subscription", subscriptionService.hasFeatureAccess(userEmail, "premium_subscription"));
        features.put("smoking_profile", subscriptionService.hasFeatureAccess(userEmail, "smoking_profile"));
        features.put("quit_plans", subscriptionService.hasFeatureAccess(userEmail, "quit_plans"));
        features.put("daily_logging", subscriptionService.hasFeatureAccess(userEmail, "daily_logging"));
        features.put("achievements", subscriptionService.hasFeatureAccess(userEmail, "achievements"));
        features.put("coach_support", subscriptionService.hasFeatureAccess(userEmail, "coach_support"));
        features.put("notifications", subscriptionService.hasFeatureAccess(userEmail, "notifications"));
        features.put("full_forum_access", subscriptionService.hasFeatureAccess(userEmail, "full_forum_access"));
        features.put("read_forum", subscriptionService.hasFeatureAccess(userEmail, "read_forum"));
        
        return ResponseEntity.ok(features);
    }
} 