package com.team04.smoking_cessation.config;

import com.team04.smoking_cessation.entity.AccountStatus;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.UserRole;
import com.team04.smoking_cessation.entity.SubscriptionPlan;
import com.team04.smoking_cessation.repository.UserRepository;
import com.team04.smoking_cessation.repository.SubscriptionPlanRepository;
import com.team04.smoking_cessation.service.AchievementService; // Để khởi tạo badges
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional; // Quan trọng để UserRepository hoạt động
@Configuration // Đánh dấu đây là một lớp cấu hình Spring
public class DataInitializer {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AchievementService achievementService; // Inject AchievementService để khởi tạo badges
    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;
    // Sử dụng CommandLineRunner để chạy code khi ứng dụng khởi động
    @Bean
    @Transactional // Đảm bảo các thao tác database trong runner này là transactional
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.findByEmail("admin@quitsmoke.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@quitsmoke.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setFullName("Front Man");
                admin.setRole(UserRole.ADMIN);
                admin.setEmailVerified(true);
                admin.setStatus(AccountStatus.ACTIVE);
                userRepository.save(admin);
                System.out.println("Default admin user created: admin@quitsmoke.com / admin");
            }

            achievementService.initializeDefaultBadges();
            System.out.println("Default badges initialized.");

            // Initialize subscription plans
            initializeSubscriptionPlans();
            System.out.println("Default subscription plans initialized.");
        };
    }

    private void initializeSubscriptionPlans() {
        // Xóa gói VIP nếu tồn tại
        subscriptionPlanRepository.findByNameIgnoreCase("VIP").ifPresent(vipPlan -> {
            subscriptionPlanRepository.delete(vipPlan);
            System.out.println("VIP plan deleted.");
        });

        // FREE Plan - Cập nhật hoặc tạo mới
        var freePlanOpt = subscriptionPlanRepository.findByNameIgnoreCase("FREE");
        if (freePlanOpt.isPresent()) {
            SubscriptionPlan freePlan = freePlanOpt.get();
            freePlan.setDescription("Gói miễn phí với các tính năng cơ bản");
            freePlan.setFeatures("[\"account_management\", \"personal_info_management\", \"premium_subscription\"]");
            subscriptionPlanRepository.save(freePlan);
            System.out.println("FREE plan updated.");
        } else {
            SubscriptionPlan freePlan = new SubscriptionPlan();
            freePlan.setName("FREE");
            freePlan.setDescription("Gói miễn phí với các tính năng cơ bản");
            freePlan.setMonthlyPrice(new java.math.BigDecimal("0"));
            freePlan.setYearlyPrice(new java.math.BigDecimal("0"));
            freePlan.setFeatures("[\"account_management\", \"personal_info_management\", \"premium_subscription\"]");
            freePlan.setIsActive(true);
            subscriptionPlanRepository.save(freePlan);
            System.out.println("FREE plan created.");
        }

        // PREMIUM Plan - Cập nhật hoặc tạo mới
        var premiumPlanOpt = subscriptionPlanRepository.findByNameIgnoreCase("PREMIUM");
        if (premiumPlanOpt.isPresent()) {
            SubscriptionPlan premiumPlan = premiumPlanOpt.get();
            premiumPlan.setDescription("Gói premium với đầy đủ tính năng hỗ trợ cai thuốc");
            premiumPlan.setFeatures("[\"smoking_profile\", \"quit_plans\", \"daily_logging\", \"achievements\", \"coach_support\", \"notifications\", \"full_forum_access\", \"read_forum\"]");
            subscriptionPlanRepository.save(premiumPlan);
            System.out.println("PREMIUM plan updated.");
        } else {
            SubscriptionPlan premiumPlan = new SubscriptionPlan();
            premiumPlan.setName("PREMIUM");
            premiumPlan.setDescription("Gói premium với đầy đủ tính năng hỗ trợ cai thuốc");
            premiumPlan.setMonthlyPrice(new java.math.BigDecimal("9.99"));
            premiumPlan.setYearlyPrice(new java.math.BigDecimal("99.99"));
            premiumPlan.setFeatures("[\"smoking_profile\", \"quit_plans\", \"daily_logging\", \"achievements\", \"coach_support\", \"notifications\", \"full_forum_access\", \"read_forum\"]");
            premiumPlan.setIsActive(true);
            subscriptionPlanRepository.save(premiumPlan);
            System.out.println("PREMIUM plan created.");
        }
    }
}

