package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.ChatMessage;
import com.team04.smoking_cessation.entity.UserRole;
import com.team04.smoking_cessation.repository.UserRepository;
import com.team04.smoking_cessation.repository.ChatMessageRepository;
import com.team04.smoking_cessation.repository.DailyLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoachService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private DailyLogRepository dailyLogRepository;

    public Map<String, Object> getCoachDashboard(String email) {
        User coach = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Coach not found"));

        Map<String, Object> dashboard = new HashMap<>();

        // Get assigned clients count
        List<User> clients = getAssignedClients(email);
        dashboard.put("totalClients", clients.size());

        // Get active sessions today
        dashboard.put("todaySessions", 0); // TODO: Implement session tracking

        // Get unread messages
        Long unreadMessages = chatMessageRepository.countUnreadMessagesForCoach(coach);
        dashboard.put("unreadMessages", unreadMessages);

        // Recent client activity
        List<Map<String, Object>> recentActivity = clients.stream()
            .limit(5)
            .map(client -> {
                Map<String, Object> activity = new HashMap<>();
                activity.put("clientName", client.getFullName());
                activity.put("lastLogin", client.getUpdatedAt());

                // Get smoke-free days for this client
                Long smokeFreeDays = dailyLogRepository.countSmokeFreeDaysByUser(client);
                activity.put("smokeFreeDays", smokeFreeDays);

                return activity;
            })
            .collect(Collectors.toList());

        dashboard.put("recentActivity", recentActivity);
        dashboard.put("coachProfile", convertToUserInfo(coach));

        return dashboard;
    }

    public List<User> getAssignedClients(String email) {
        // For now, return premium members who need coaching
        // In a real system, you'd have a coach-client assignment table
        return userRepository.findByRole(UserRole.MEMBER)
            .stream()
            .filter(user -> user.getSubscriptions() != null && !user.getSubscriptions().isEmpty())
            .collect(Collectors.toList());
    }

    public Map<String, Object> getClientProfile(String coachEmail, Long clientId) {
        User coach = userRepository.findByEmail(coachEmail)
            .orElseThrow(() -> new RuntimeException("Coach not found"));

        User client = userRepository.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("clientInfo", convertToUserInfo(client));

        // Get client's smoking statistics
        Long smokeFreeDays = dailyLogRepository.countSmokeFreeDaysByUser(client);
        profile.put("smokeFreeDays", smokeFreeDays);

        // Get recent daily logs
        profile.put("recentLogs", dailyLogRepository.findByUserOrderByLogDateDesc(client)
            .stream().limit(7).collect(Collectors.toList()));

        // Get active quit plan
        profile.put("activeQuitPlan", client.getQuitPlans().stream()
            .filter(plan -> plan.getStatus().name().equals("ACTIVE"))
            .findFirst().orElse(null));

        return profile;
    }

    public Map<String, Object> createCoachingSession(String coachEmail, Map<String, Object> sessionData) {
        User coach = userRepository.findByEmail(coachEmail)
            .orElseThrow(() -> new RuntimeException("Coach not found"));

        // In a real implementation, you'd create a CoachingSession entity
        Map<String, Object> session = new HashMap<>();
        session.put("id", System.currentTimeMillis()); // Mock ID
        session.put("coachId", coach.getId());
        session.put("clientId", sessionData.get("clientId"));
        session.put("sessionType", sessionData.get("sessionType")); // "ONE_ON_ONE", "GROUP"
        session.put("scheduledTime", sessionData.get("scheduledTime"));
        session.put("duration", sessionData.get("duration"));
        session.put("status", "SCHEDULED");
        session.put("createdAt", LocalDateTime.now());

        return session;
    }

    public List<Map<String, Object>> getCoachingSessions(String coachEmail) {
        // Mock implementation - in real system, query CoachingSession table
        return List.of(); // Return empty list for now
    }

    public ChatMessage sendMessageToClient(String coachEmail, Long clientId, String content) {
        User coach = userRepository.findByEmail(coachEmail)
            .orElseThrow(() -> new RuntimeException("Coach not found"));

        User client = userRepository.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        ChatMessage message = new ChatMessage(coach, client, content);
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getChatHistory(String coachEmail, Long clientId) {
        User coach = userRepository.findByEmail(coachEmail)
            .orElseThrow(() -> new RuntimeException("Coach not found"));

        User client = userRepository.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        return chatMessageRepository.findChatHistory(coach, client);
    }

    public void addCoachingNotes(String coachEmail, Long clientId, String notes) {
        // In a real implementation, you'd save to a CoachingNotes entity
        // For now, we'll just validate the coach and client exist
        User coach = userRepository.findByEmail(coachEmail)
            .orElseThrow(() -> new RuntimeException("Coach not found"));

        User client = userRepository.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        // Notes would be saved here
    }

    public Map<String, Object> getCoachStatistics(String coachEmail) {
        User coach = userRepository.findByEmail(coachEmail)
            .orElseThrow(() -> new RuntimeException("Coach not found"));

        Map<String, Object> stats = new HashMap<>();

        List<User> clients = getAssignedClients(coachEmail);
        stats.put("totalClients", clients.size());
        stats.put("totalSessions", 0); // TODO: Count from sessions table
        stats.put("averageRating", 4.5); // TODO: Calculate from ratings

        // Client success rate
        long successfulClients = clients.stream()
            .mapToLong(client -> dailyLogRepository.countSmokeFreeDaysByUser(client))
            .filter(days -> days >= 30)
            .count();

        stats.put("successRate", clients.isEmpty() ? 0 : (successfulClients * 100.0 / clients.size()));

        return stats;
    }

    public void setCoachAvailability(String coachEmail, Map<String, Object> availabilityData) {
        User coach = userRepository.findByEmail(coachEmail)
            .orElseThrow(() -> new RuntimeException("Coach not found"));

        // In a real implementation, save to CoachAvailability entity
        // availabilityData would contain: days, hours, timezone, etc.
    }

    private Map<String, Object> convertToUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("fullName", user.getFullName());
        info.put("email", user.getEmail());
        info.put("avatarUrl", user.getAvatarUrl());
        info.put("role", user.getRole());
        info.put("createdAt", user.getCreatedAt());
        return info;
    }
}
