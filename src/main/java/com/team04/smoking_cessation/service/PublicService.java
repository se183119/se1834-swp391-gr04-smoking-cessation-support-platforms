package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.*;
import com.team04.smoking_cessation.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private EmailService emailService;

    public Map<String, Object> getPlatformInfo() {
        Map<String, Object> info = new HashMap<>();

        info.put("platformName", "Smoking Cessation Support Platform");
        info.put("description", "Nền tảng hỗ trợ cai nghiện thuốc lá toàn diện với AI, cộng đồng và huấn luyện viên chuyên nghiệp");
        info.put("version", "1.0.0");

        // Platform features
        info.put("features", List.of(
            "Theo dõi tiến trình cai thuốc hàng ngày",
            "Kế hoạch cai thuốc được tạo bởi AI",
            "Hệ thống gamification với huy hiệu thành tích",
            "Cộng đồng hỗ trợ và chia sẻ kinh nghiệm",
            "Tư vấn 1-1 với huấn luyện viên chuyên nghiệp",
            "Thống kê và phân tích chi tiết"
        ));

        return info;
    }

    public List<Map<String, Object>> getPublicLeaderboard() {
        // Get top performers (anonymized for privacy)
        return achievementRepository.getTopUsersByAchievementCount()
            .stream()
            .limit(10)
            .map(result -> {
                Map<String, Object> entry = new HashMap<>();
                entry.put("rank", 1); // Calculate based on position
                entry.put("userName", "User****"); // Anonymized
                entry.put("achievementCount", result[2]);
                entry.put("smokeFreeDays", result[3]);
                return entry;
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSuccessStories() {
        // Get success stories from forum posts
        return forumPostRepository.findByCategoryAndParentPostIsNullAndStatusOrderByCreatedAtDesc(
            ForumPost.PostCategory.SUCCESS_STORIES, ForumPost.PostStatus.ACTIVE, null)
            .getContent()
            .stream()
            .limit(5)
            .map(post -> {
                Map<String, Object> story = new HashMap<>();
                story.put("id", post.getId());
                story.put("title", post.getTitle());
                story.put("excerpt", post.getContent().substring(0, Math.min(200, post.getContent().length())) + "...");
                story.put("author", "Anonymous User"); // Privacy protection
                story.put("date", post.getCreatedAt());
                story.put("likes", post.getLikeCount());
                return story;
            })
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSubscriptionPlans() {
        return subscriptionPlanRepository.findActiveOrderByPrice()
            .stream()
            .map(plan -> {
                Map<String, Object> planInfo = new HashMap<>();
                planInfo.put("id", plan.getId());
                planInfo.put("name", plan.getName());
                planInfo.put("description", plan.getDescription());
                planInfo.put("monthlyPrice", plan.getMonthlyPrice());
                planInfo.put("yearlyPrice", plan.getYearlyPrice());
                planInfo.put("features", plan.getFeatures());
                planInfo.put("popular", "PREMIUM".equals(plan.getName())); // Mark premium as popular
                return planInfo;
            })
            .collect(Collectors.toList());
    }

    public Map<String, Object> getPlatformStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // User statistics
        Long totalUsers = userRepository.countByRoleAndStatus(UserRole.MEMBER, AccountStatus.ACTIVE);
        stats.put("totalUsers", totalUsers);

        // Success statistics
        Long usersWithProgress = dailyLogRepository.countUsersWithSmokeFreeDays();
        stats.put("successfulUsers", usersWithProgress);

        // Calculate success rate
        double successRate = totalUsers > 0 ? (usersWithProgress * 100.0 / totalUsers) : 0;
        stats.put("successRate", Math.round(successRate));

        // Community statistics
        stats.put("totalPosts", forumPostRepository.count());
        stats.put("totalAchievements", achievementRepository.count());

        // Available coaches
        Long totalCoaches = userRepository.countByRoleAndStatus(UserRole.COACH, AccountStatus.ACTIVE);
        stats.put("availableCoaches", totalCoaches);

        return stats;
    }

    public List<Map<String, Object>> getBlogPosts() {
        // Mock blog posts - in real system, you'd have a BlogPost entity
        return List.of(
            Map.of(
                "id", 1,
                "title", "10 Mẹo Vượt Qua Cơn Thèm Thuốc Lá",
                "excerpt", "Khám phá những chiến lược hiệu quả để kiểm soát cơn thèm thuốc lá trong quá trình cai nghiện...",
                "author", "Dr. Nguyễn Văn A",
                "publishDate", "2024-01-15",
                "category", "Tips & Tricks",
                "readTime", "5 phút"
            ),
            Map.of(
                "id", 2,
                "title", "Tác Hại Của Thuốc Lá Và Lợi Ích Khi Cai",
                "excerpt", "Tìm hiểu về những tác hại nghiêm trọng của thuốc lá và những lợi ích tuyệt vời khi bạn quyết định cai...",
                "author", "BS. Trần Thị B",
                "publishDate", "2024-01-10",
                "category", "Health",
                "readTime", "8 phút"
            )
        );
    }

    public List<Map<String, Object>> getTestimonials() {
        // Mock testimonials - in real system, get from verified users
        return List.of(
            Map.of(
                "name", "Anh Minh",
                "age", 35,
                "story", "Sau 15 năm hút thuốc, tôi đã cai thành công nhờ nền tảng này. Hệ thống theo dõi và cộng đồng hỗ trợ rất tuyệt vời!",
                "smokeFreeDays", 180,
                "moneySaved", 2700000,
                "location", "Hà Nội"
            ),
            Map.of(
                "name", "Chị Lan",
                "age", 28,
                "story", "Kế hoạch AI và coach 1-1 đã giúp tôi vượt qua những thời điểm khó khăn nhất. Cảm ơn platform!",
                "smokeFreeDays", 90,
                "moneySaved", 1350000,
                "location", "TP.HCM"
            )
        );
    }

    public List<Map<String, Object>> getAvailableCoaches() {
        return userRepository.findByRole(UserRole.COACH)
            .stream()
            .filter(coach -> coach.getStatus() == AccountStatus.ACTIVE)
            .map(coach -> {
                Map<String, Object> coachInfo = new HashMap<>();
                coachInfo.put("id", coach.getId());
                coachInfo.put("name", coach.getFullName());
                coachInfo.put("experience", "5+ năm kinh nghiệm");
                coachInfo.put("specialization", "Cai nghiện thuốc lá");
                coachInfo.put("rating", 4.8);
                coachInfo.put("totalClients", 50); // Mock data
                coachInfo.put("successRate", 85); // Mock data
                coachInfo.put("available", true);
                return coachInfo;
            })
            .collect(Collectors.toList());
    }

    public void sendContactMessage(Map<String, String> contactData) {
        String name = contactData.get("name");
        String email = contactData.get("email");
        String subject = contactData.get("subject");
        String message = contactData.get("message");

        // Send email to admin or support team
        emailService.sendContactMessage(email, "Tin nhắn liên hệ từ " + name + ": " + subject + "\n\n" + message);
    }

    public List<Map<String, Object>> getFAQ() {
        return List.of(
            Map.of(
                "question", "Nền tảng này có miễn phí không?",
                "answer", "Chúng tôi có gói miễn phí với các tính năng cơ bản. Gói Premium cung cấp thêm nhiều tính năng nâng cao và hỗ trợ từ coach."
            ),
            Map.of(
                "question", "Làm thế nào để đăng ký tài khoản?",
                "answer", "Bạn chỉ cần cung cấp email và mật khẩu, sau đó xác thực email để kích hoạt tài khoản."
            ),
            Map.of(
                "question", "Tôi có thể hủy đăng ký bất cứ lúc nào không?",
                "answer", "Có, bạn có thể hủy đăng ký bất cứ lúc nào từ trang quản lý tài khoản của mình."
            ),
            Map.of(
                "question", "Dữ liệu của tôi có được bảo mật không?",
                "answer", "Chúng tôi cam kết bảo vệ thông tin cá nhân của bạn theo các tiêu chuẩn bảo mật cao nhất."
            )
        );
    }
}
