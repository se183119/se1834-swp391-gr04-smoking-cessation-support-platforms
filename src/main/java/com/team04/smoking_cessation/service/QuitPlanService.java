package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.QuitPlan;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.SmokingProfile;
import com.team04.smoking_cessation.repository.QuitPlanRepository;
import com.team04.smoking_cessation.repository.UserRepository;
import com.team04.smoking_cessation.repository.SmokingProfileRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class QuitPlanService {

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SmokingProfileRepository smokingProfileRepository;



    public QuitPlan createQuitPlan(String email, Map<String, Object> planData) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        QuitPlan quitPlan = new QuitPlan();
        quitPlan.setUser(user);
        quitPlan.setTitle((String) planData.get("title"));
        quitPlan.setDescription((String) planData.get("description"));

        if (planData.containsKey("quitMethod")) {
            quitPlan.setQuitMethod(QuitPlan.QuitMethod.valueOf((String) planData.get("quitMethod")));
        }
        if (planData.containsKey("quitDate")) {
            quitPlan.setQuitDate(LocalDate.parse((String) planData.get("quitDate")));
        }
        if (planData.containsKey("targetDate")) {
            quitPlan.setTargetDate(LocalDate.parse((String) planData.get("targetDate")));
        }
        if (planData.containsKey("personalReasons")) {
            quitPlan.setPersonalReasons((String) planData.get("personalReasons"));
        }
        if (planData.containsKey("copingStrategies")) {
            quitPlan.setCopingStrategies((String) planData.get("copingStrategies"));
        }

        return quitPlanRepository.save(quitPlan);
    }

    public QuitPlan generateAiQuitPlan(String email, Map<String, Object> userPreferences) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<SmokingProfile> profileOpt = smokingProfileRepository.findByUserAndIsActiveTrue(user);

        QuitPlan aiPlan = new QuitPlan();
        aiPlan.setUser(user);
        aiPlan.setIsAiGenerated(true);
        aiPlan.setTitle("AI-Generated Personalized Quit Plan");

        // AI logic based on user's smoking profile and preferences
        if (profileOpt.isPresent()) {
            SmokingProfile profile = profileOpt.get();

            // Determine quit method based on smoking intensity and user preferences
            String preferredMethod = (String) userPreferences.get("preferredMethod");
            QuitPlan.QuitMethod quitMethod;
            String description;

            if (preferredMethod != null && !preferredMethod.isEmpty()) {
                try {
                    quitMethod = QuitPlan.QuitMethod.valueOf(preferredMethod.toUpperCase());
                } catch (IllegalArgumentException e) {
                    quitMethod = determineOptimalQuitMethod(profile);
                }
            } else {
                quitMethod = determineOptimalQuitMethod(profile);
            }

            // Generate personalized description
            description = generatePersonalizedDescription(profile, quitMethod);
            aiPlan.setQuitMethod(quitMethod);
            aiPlan.setDescription(description);

            // Set timeline based on smoking intensity and user preferences
            LocalDate quitDate = determineQuitDate(userPreferences);
            LocalDate targetDate = determineTargetDate(profile, quitDate, userPreferences);

            aiPlan.setQuitDate(quitDate);
            aiPlan.setTargetDate(targetDate);

            // Generate personalized coping strategies
            String copingStrategies = generatePersonalizedCopingStrategies(profile, quitMethod);
            aiPlan.setCopingStrategies(copingStrategies);

            // Generate personalized reasons
            String personalReasons = generatePersonalizedReasons(profile, userPreferences);
            aiPlan.setPersonalReasons(personalReasons);

        } else {
            // Default AI plan if no profile
            aiPlan.setQuitMethod(QuitPlan.QuitMethod.GRADUAL_REDUCTION);
            aiPlan.setQuitDate(LocalDate.now().plusDays(7));
            aiPlan.setTargetDate(LocalDate.now().plusWeeks(10));
            aiPlan.setDescription("Personalized quit plan based on best practices for smoking cessation. Please complete your smoking profile for more personalized recommendations.");
            
            // Default strategies
            aiPlan.setCopingStrategies("[\"Deep breathing exercises\", \"Physical exercise\", \"Healthy snacking\", \"Meditation\", \"Call a friend\", \"Drink water\", \"Take a walk\"]");
            aiPlan.setPersonalReasons("[\"Better health\", \"Save money\", \"Family\", \"Self-improvement\", \"Longer life\", \"Better breathing\"]");
        }

        return quitPlanRepository.save(aiPlan);
    }

    private QuitPlan.QuitMethod determineOptimalQuitMethod(SmokingProfile profile) {
        if (profile.getCigarettesPerDay() > 20) {
            return QuitPlan.QuitMethod.GRADUAL_REDUCTION;
        } else if (profile.getCigarettesPerDay() > 10) {
            return QuitPlan.QuitMethod.NICOTINE_REPLACEMENT;
        } else {
            return QuitPlan.QuitMethod.COLD_TURKEY;
        }
    }

    private String generatePersonalizedDescription(SmokingProfile profile, QuitPlan.QuitMethod method) {
        StringBuilder description = new StringBuilder();
        
        description.append("Based on your smoking pattern (");
        description.append(profile.getCigarettesPerDay()).append(" cigarettes/day, ");
        description.append(profile.getYearsOfSmoking()).append(" years of smoking), ");
        
        switch (method) {
            case GRADUAL_REDUCTION:
                description.append("we recommend gradual reduction to minimize withdrawal symptoms. ");
                description.append("This approach will help your body adjust gradually while reducing cravings.");
                break;
            case NICOTINE_REPLACEMENT:
                description.append("nicotine replacement therapy can help you manage cravings effectively. ");
                description.append("This method provides controlled nicotine doses while you break the smoking habit.");
                break;
            case COLD_TURKEY:
                description.append("cold turkey approach can be highly effective for your smoking level. ");
                description.append("Your determination combined with our support will maximize your success rate.");
                break;
        }
        
        if (profile.getPreviousQuitAttempts() > 0) {
            description.append(" We've considered your previous quit attempts and tailored this plan accordingly.");
        }
        
        return description.toString();
    }

    private LocalDate determineQuitDate(Map<String, Object> userPreferences) {
        if (userPreferences.containsKey("preferredQuitDate")) {
            try {
                return LocalDate.parse((String) userPreferences.get("preferredQuitDate"));
            } catch (Exception e) {
                // Fall back to default
            }
        }
        return LocalDate.now().plusDays(7); // One week preparation
    }

    private LocalDate determineTargetDate(SmokingProfile profile, LocalDate quitDate, Map<String, Object> userPreferences) {
        int weeksToAdd;
        
        if (profile.getCigarettesPerDay() > 15) {
            weeksToAdd = 12; // 3 months for heavy smokers
        } else if (profile.getCigarettesPerDay() > 8) {
            weeksToAdd = 10; // 2.5 months for moderate smokers
        } else {
            weeksToAdd = 8; // 2 months for light smokers
        }
        
        return quitDate.plusWeeks(weeksToAdd);
    }

    private String generatePersonalizedCopingStrategies(SmokingProfile profile, QuitPlan.QuitMethod method) {
        StringBuilder strategies = new StringBuilder("[");
        
        // Base strategies
        strategies.append("\"Deep breathing exercises\", ");
        strategies.append("\"Physical exercise\", ");
        strategies.append("\"Healthy snacking\", ");
        strategies.append("\"Meditation\", ");
        strategies.append("\"Call a friend\", ");
        
        // Method-specific strategies
        switch (method) {
            case GRADUAL_REDUCTION:
                strategies.append("\"Track daily reduction\", ");
                strategies.append("\"Celebrate small wins\", ");
                break;
            case NICOTINE_REPLACEMENT:
                strategies.append("\"Use nicotine patches/gum\", ");
                strategies.append("\"Monitor nicotine intake\", ");
                break;
            case COLD_TURKEY:
                strategies.append("\"Stay busy\", ");
                strategies.append("\"Avoid triggers\", ");
                break;
        }
        
        // Profile-specific strategies
        if (profile.getStressLevel() != null && profile.getStressLevel() > 7) {
            strategies.append("\"Stress management techniques\", ");
        }
        
        if (profile.getCurrentWeight() != null) {
            strategies.append("\"Weight management\", ");
        }
        
        strategies.append("\"Drink plenty of water\"");
        strategies.append("]");
        
        return strategies.toString();
    }

    private String generatePersonalizedReasons(SmokingProfile profile, Map<String, Object> userPreferences) {
        StringBuilder reasons = new StringBuilder("[");
        
        // Base reasons
        reasons.append("\"Better health\", ");
        reasons.append("\"Save money\", ");
        
        // Calculate money saved
        if (profile.getCigarettesPerDay() != null && profile.getPricePerPack() != null && profile.getCigarettesPerPack() != null) {
            double dailyCost = (profile.getCigarettesPerDay() * profile.getPricePerPack().doubleValue()) / profile.getCigarettesPerPack();
            double monthlyCost = dailyCost * 30;
            reasons.append("\"Save $").append(String.format("%.0f", monthlyCost)).append("/month\", ");
        }
        
        // Health-specific reasons
        if (profile.getYearsOfSmoking() > 10) {
            reasons.append("\"Reduce long-term health risks\", ");
        }
        
        if (profile.getHealthConcerns() != null && !profile.getHealthConcerns().isEmpty()) {
            reasons.append("\"Address health concerns\", ");
        }
        
        // User preference reasons
        if (userPreferences.containsKey("personalReasons")) {
            String personalReasons = (String) userPreferences.get("personalReasons");
            if (personalReasons != null && !personalReasons.isEmpty()) {
                reasons.append("\"").append(personalReasons).append("\", ");
            }
        }
        
        reasons.append("\"Family\", ");
        reasons.append("\"Self-improvement\"");
        reasons.append("]");
        
        return reasons.toString();
    }

    public List<QuitPlan> getUserQuitPlans(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return quitPlanRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public QuitPlan getActiveQuitPlan(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return quitPlanRepository.findActiveQuitPlanByUser(user)
            .orElseThrow(() -> new RuntimeException("No active quit plan found"));
    }

    public QuitPlan getQuitPlanById(String email, Long planId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        QuitPlan plan = quitPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Quit plan not found"));

        if (!plan.getUser().equals(user)) {
            throw new RuntimeException("Access denied: This quit plan belongs to another user");
        }

        return plan;
    }

    public QuitPlan updateQuitPlan(String email, Long planId, Map<String, Object> planData) {
        QuitPlan plan = getQuitPlanById(email, planId);

        if (planData.containsKey("title")) {
            plan.setTitle((String) planData.get("title"));
        }
        if (planData.containsKey("description")) {
            plan.setDescription((String) planData.get("description"));
        }
        if (planData.containsKey("quitDate")) {
            plan.setQuitDate(LocalDate.parse((String) planData.get("quitDate")));
        }
        if (planData.containsKey("targetDate")) {
            plan.setTargetDate(LocalDate.parse((String) planData.get("targetDate")));
        }

        return quitPlanRepository.save(plan);
    }

    public void activateQuitPlan(String email, Long planId) {
        // First, deactivate any existing active plans
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        List<QuitPlan> userPlans = quitPlanRepository.findByUserOrderByCreatedAtDesc(user);
        userPlans.forEach(plan -> {
            if (plan.getStatus() == QuitPlan.PlanStatus.ACTIVE) {
                plan.setStatus(QuitPlan.PlanStatus.PAUSED);
                quitPlanRepository.save(plan);
            }
        });

        // Activate the selected plan
        QuitPlan plan = getQuitPlanById(email, planId);
        plan.setStatus(QuitPlan.PlanStatus.ACTIVE);
        quitPlanRepository.save(plan);
    }

    public void completeQuitPlan(String email, Long planId) {
        QuitPlan plan = getQuitPlanById(email, planId);
        plan.setStatus(QuitPlan.PlanStatus.COMPLETED);
        quitPlanRepository.save(plan);
    }

    public void deleteQuitPlan(String email, Long planId) {
        QuitPlan plan = getQuitPlanById(email, planId);
        quitPlanRepository.delete(plan);
    }

    public String generateCopingStrategies(String email, String quitMethod) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<SmokingProfile> profileOpt = smokingProfileRepository.findByUserAndIsActiveTrue(user);
        
        if (profileOpt.isPresent()) {
            return generateFallbackCopingStrategies(profileOpt.get(), quitMethod);
        } else {
            return "Deep breathing, physical exercise, healthy snacking, meditation, and calling a friend are effective coping strategies.";
        }
    }

    private String generateFallbackCopingStrategies(SmokingProfile profile, String quitMethod) {
        StringBuilder strategies = new StringBuilder();
        strategies.append("Based on your smoking profile, here are personalized coping strategies:\n\n");
        
        strategies.append("1. Deep breathing exercises - Take 10 deep breaths when cravings hit\n");
        strategies.append("2. Physical exercise - Go for a walk or do some light exercise\n");
        strategies.append("3. Healthy snacking - Keep healthy snacks like nuts or fruits nearby\n");
        strategies.append("4. Meditation - Practice mindfulness for 5-10 minutes daily\n");
        strategies.append("5. Call a friend - Reach out to supportive friends or family\n");
        strategies.append("6. Drink water - Stay hydrated throughout the day\n");
        strategies.append("7. Distract yourself - Engage in hobbies or activities you enjoy\n");
        
        if (profile.getSmokingTriggers() != null && !profile.getSmokingTriggers().isEmpty()) {
            strategies.append("\nSpecific strategies for your triggers:\n");
            strategies.append("- Avoid or modify situations that trigger smoking\n");
            strategies.append("- Replace smoking with alternative activities\n");
        }
        
        return strategies.toString();
    }

    /**
     * Trả về mẫu kế hoạch cai thuốc chuẩn (không cá nhân hóa, không AI)
     */
    public QuitPlan getTemplateQuitPlan() {
        QuitPlan template = new QuitPlan();
        template.setTitle("Mẫu kế hoạch cai thuốc chuẩn");
        template.setDescription("Kế hoạch gồm các giai đoạn: Chuẩn bị, Ngưng từng phần, Duy trì. Hãy tùy chỉnh cho phù hợp với bản thân.");
        template.setQuitMethod(QuitPlan.QuitMethod.GRADUAL_REDUCTION);
        template.setQuitDate(LocalDate.now().plusDays(7));
        template.setTargetDate(LocalDate.now().plusWeeks(10));
        template.setPersonalReasons("[\"Bảo vệ sức khỏe\", \"Tiết kiệm tiền\", \"Vì gia đình\"]");
        template.setCopingStrategies("[\"Tập thể dục\", \"Uống nước\", \"Tránh môi trường có khói thuốc\", \"Tham gia hoạt động mới\"]");
        template.setRewardSystem("[\"Tự thưởng sau mỗi tuần không hút thuốc\", \"Chia sẻ thành tích với bạn bè\"]");
        template.setDailyCigaretteLimit(5);
        template.setIsAiGenerated(false);
        return template;
    }
}
