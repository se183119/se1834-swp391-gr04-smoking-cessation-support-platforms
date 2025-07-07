package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.*;
import com.team04.smoking_cessation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SubscriptionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PayosService payosService;

    /**
     * Lấy danh sách subscription plans
     */
    public List<SubscriptionPlan> getAvailablePlans() {
        return subscriptionPlanRepository.findByIsActiveTrue();
    }

    /**
     * Lấy subscription plan theo ID
     */
    public SubscriptionPlan getPlanById(Long planId) {
        return subscriptionPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Subscription plan not found"));
    }

    /**
     * Lấy subscription hiện tại của user
     */
    public Optional<UserSubscription> getCurrentSubscription(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        return userSubscriptionRepository.findByUserAndStatus(user, UserSubscription.SubscriptionStatus.ACTIVE);
    }

    /**
     * Tạo link thanh toán PayOS cho subscription
     */
    public Map<String, Object> createPaymentLink(String userEmail, Long planId, String billingCycle, String returnUrl, String cancelUrl) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        SubscriptionPlan plan = getPlanById(planId);
        BigDecimal amount = "MONTHLY".equals(billingCycle) ? plan.getMonthlyPrice() : plan.getYearlyPrice();
        
        // Tạo order code duy nhất
        String orderCode = "SUB_" + user.getId() + "_" + planId + "_" + System.currentTimeMillis();
        
        // Tạo description
        String description = "Đăng ký gói " + plan.getName() + " (" + billingCycle + ")";
        
        // Gọi PayOS tạo link thanh toán
        Map<String, Object> payosResponse = payosService.createPaymentLink(orderCode, amount, description, returnUrl, cancelUrl);
        
        // Lưu thông tin subscription tạm thời (PENDING)
        UserSubscription userSubscription = new UserSubscription();
        userSubscription.setUser(user);
        userSubscription.setPlan(plan);
        userSubscription.setStatus(UserSubscription.SubscriptionStatus.PENDING);
        userSubscription.setBillingCycle(UserSubscription.BillingCycle.valueOf(billingCycle.toUpperCase()));
        userSubscription.setStartDate(LocalDateTime.now());
        userSubscription.setAmount(amount);
        userSubscription.setPaymentMethod("PAYOS");
        userSubscription.setOrderCode(orderCode);
        
        // Tính toán ngày kết thúc
        LocalDateTime endDate = "MONTHLY".equals(billingCycle) 
            ? LocalDateTime.now().plusMonths(1) 
            : LocalDateTime.now().plusYears(1);
        userSubscription.setEndDate(endDate);
        userSubscription.setNextBillingDate(endDate);
        
        userSubscriptionRepository.save(userSubscription);
        
        // Trả về thông tin cho frontend
        Map<String, Object> response = new HashMap<>();
        response.put("subscriptionId", userSubscription.getId());
        response.put("orderCode", orderCode);
        response.put("paymentUrl", payosResponse.get("data"));
        response.put("amount", amount);
        response.put("plan", plan.getName());
        response.put("billingCycle", billingCycle);
        
        return response;
    }

    /**
     * Xử lý webhook từ PayOS khi thanh toán thành công
     */
    public void handlePayosWebhook(String orderCode, String transactionId, String status) {
        // Tìm subscription theo orderCode
        Optional<UserSubscription> subscriptionOpt = userSubscriptionRepository.findByOrderCode(orderCode);
        
        if (subscriptionOpt.isEmpty()) {
            throw new RuntimeException("Subscription not found for orderCode: " + orderCode);
        }
        
        UserSubscription userSubscription = subscriptionOpt.get();
        
        if ("PAID".equals(status)) {
            // Cập nhật trạng thái thành ACTIVE
            userSubscription.setStatus(UserSubscription.SubscriptionStatus.ACTIVE);
            userSubscription.setPayosTransactionId(transactionId);
            userSubscriptionRepository.save(userSubscription);
            
            // Gửi email xác nhận
            sendSubscriptionConfirmationEmail(userSubscription.getUser(), userSubscription.getPlan());
        } else if ("CANCELLED".equals(status) || "FAILED".equals(status)) {
            // Cập nhật trạng thái thành CANCELLED
            userSubscription.setStatus(UserSubscription.SubscriptionStatus.CANCELLED);
            userSubscriptionRepository.save(userSubscription);
        }
    }

    /**
     * Hủy subscription
     */
    public void cancelSubscription(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<UserSubscription> currentSubscription = getCurrentSubscription(userEmail);
        if (currentSubscription.isEmpty()) {
            throw new RuntimeException("No active subscription found");
        }

        UserSubscription subscription = currentSubscription.get();

        // Cập nhật trạng thái trong database
        subscription.setStatus(UserSubscription.SubscriptionStatus.CANCELLED);
        subscription.setAutoRenew(false);
        userSubscriptionRepository.save(subscription);

        // Gửi email xác nhận hủy
        sendSubscriptionCancellationEmail(user, subscription.getPlan());
    }

    /**
     * Cập nhật subscription (upgrade/downgrade)
     */
    public Map<String, Object> updateSubscription(String userEmail, Long newPlanId) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<UserSubscription> currentSubscription = getCurrentSubscription(userEmail);
        if (currentSubscription.isEmpty()) {
            throw new RuntimeException("No active subscription found");
        }

        SubscriptionPlan newPlan = getPlanById(newPlanId);
        UserSubscription subscription = currentSubscription.get();

        // Cập nhật database
        subscription.setPlan(newPlan);
        subscription.setAmount("MONTHLY".equals(subscription.getBillingCycle().name()) 
            ? newPlan.getMonthlyPrice() 
            : newPlan.getYearlyPrice());
        userSubscriptionRepository.save(subscription);

        // Gửi email thông báo
        sendSubscriptionUpdateEmail(user, newPlan);

        Map<String, Object> response = new HashMap<>();
        response.put("subscriptionId", subscription.getId());
        response.put("newPlan", newPlan.getName());
        response.put("status", "updated");

        return response;
    }

    /**
     * Kiểm tra quyền truy cập feature
     */
    public boolean hasFeatureAccess(String userEmail, String feature) {
        Optional<UserSubscription> subscription = getCurrentSubscription(userEmail);
        
        if (subscription.isEmpty()) {
            return isFreeFeature(feature);
        }

        UserSubscription.SubscriptionStatus status = subscription.get().getStatus();
        if (status != UserSubscription.SubscriptionStatus.ACTIVE) {
            return isFreeFeature(feature);
        }

        String planName = subscription.get().getPlan().getName();
        
        // FREE features
        if (isFreeFeature(feature)) {
            return true;
        }

        // PREMIUM features
        if ("PREMIUM".equals(planName)) {
            return isPremiumFeature(feature);
        }

        // All other features are for PREMIUM only
        return false;
    }

    /**
     * Lấy thông tin billing
     */
    public Map<String, Object> getBillingInfo(String userEmail) {
        Optional<UserSubscription> subscription = getCurrentSubscription(userEmail);
        
        Map<String, Object> billingInfo = new HashMap<>();
        
        if (subscription.isPresent()) {
            UserSubscription sub = subscription.get();
            billingInfo.put("planName", sub.getPlan().getName());
            billingInfo.put("amount", sub.getAmount());
            billingInfo.put("billingCycle", sub.getBillingCycle());
            billingInfo.put("nextBillingDate", sub.getNextBillingDate());
            billingInfo.put("autoRenew", sub.getAutoRenew());
            billingInfo.put("status", sub.getStatus());
        } else {
            billingInfo.put("planName", "FREE");
            billingInfo.put("amount", 0);
            billingInfo.put("billingCycle", "N/A");
            billingInfo.put("nextBillingDate", null);
            billingInfo.put("autoRenew", false);
            billingInfo.put("status", "FREE");
        }

        return billingInfo;
    }

    private boolean isFreeFeature(String feature) {
        return List.of(
            "account_management",
            "personal_info_management",
            "premium_subscription"
        ).contains(feature);
    }

    private boolean isPremiumFeature(String feature) {
        return List.of(
            "smoking_profile",
            "quit_plans",
            "daily_logging",
            "achievements",
            "coach_support",
            "notifications",
            "full_forum_access",
            "read_forum"
        ).contains(feature);
    }

    private void sendSubscriptionConfirmationEmail(User user, SubscriptionPlan plan) {
        String subject = "Welcome to " + plan.getName() + " Plan! 🎉";
        String message = "Hello " + user.getFullName() + ",\n\n" +
            "Thank you for subscribing to our " + plan.getName() + " plan!\n\n" +
            "You now have access to all premium features:\n" +
            "• Personalized quit plans\n" +
            "• Daily progress tracking\n" +
            "• Achievement system\n" +
            "• Coach support\n" +
            "• Full community access\n\n" +
            "Start your journey to a smoke-free life today!\n\n" +
            "Best regards,\n" +
            "Your Smoking Cessation Support Team";

        emailService.sendMotivationalMessage(user.getEmail(), message);
    }

    private void sendSubscriptionCancellationEmail(User user, SubscriptionPlan plan) {
        String subject = "Subscription Cancelled";
        String message = "Hello " + user.getFullName() + ",\n\n" +
            "Your " + plan.getName() + " subscription has been cancelled.\n\n" +
            "You can still access free features, and you can resubscribe anytime.\n\n" +
            "We hope to see you again soon!\n\n" +
            "Best regards,\n" +
            "Your Smoking Cessation Support Team";

        emailService.sendMotivationalMessage(user.getEmail(), message);
    }

    private void sendSubscriptionUpdateEmail(User user, SubscriptionPlan plan) {
        String subject = "Subscription Updated to " + plan.getName();
        String message = "Hello " + user.getFullName() + ",\n\n" +
            "Your subscription has been successfully updated to " + plan.getName() + ".\n\n" +
            "Enjoy your new features!\n\n" +
            "Best regards,\n" +
            "Your Smoking Cessation Support Team";

        emailService.sendMotivationalMessage(user.getEmail(), message);
    }
} 