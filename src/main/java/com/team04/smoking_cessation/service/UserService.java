package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.dto.response.UserResponse;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.entity.AccountStatus;
import com.team04.smoking_cessation.entity.Gender;
import com.team04.smoking_cessation.repository.UserRepository;
import com.team04.smoking_cessation.repository.DailyLogRepository;
import com.team04.smoking_cessation.repository.QuitPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DailyLogRepository dailyLogRepository;

    @Autowired
    private QuitPlanRepository quitPlanRepository;

    private final String uploadDir = "uploads/avatars/";

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToUserResponse(user);
    }

    public UserResponse updateUserProfile(String email, Map<String, Object> updateData) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateData.containsKey("fullName")) {
            user.setFullName((String) updateData.get("fullName"));
        }
        if (updateData.containsKey("phone")) {
            user.setPhone((String) updateData.get("phone"));
        }
        if (updateData.containsKey("age")) {
            user.setAge((Integer) updateData.get("age"));
        }
        if (updateData.containsKey("gender")) {
            user.setGender(Gender.valueOf((String) updateData.get("gender")));
        }
        if (updateData.containsKey("occupation")) {
            user.setOccupation((String) updateData.get("occupation"));
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    public String uploadAvatar(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Save file
            Files.copy(file.getInputStream(), filePath);

            // Update user avatar URL
            String avatarUrl = "/uploads/avatars/" + fileName;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload avatar: " + e.getMessage());
        }
    }

    public Map<String, Object> getUserDashboard(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> dashboard = new HashMap<>();

        // Get smoke-free days
        Long smokeFreeDays = dailyLogRepository.countSmokeFreeDaysByUser(user);
        dashboard.put("smokeFreeDays", smokeFreeDays);

        // Get active quit plan
        var activeQuitPlan = quitPlanRepository.findActiveQuitPlanByUser(user);
        dashboard.put("hasActiveQuitPlan", activeQuitPlan.isPresent());

        // Calculate money saved (placeholder calculation)
        dashboard.put("moneySaved", smokeFreeDays * 5.0); // Assume $5 per day

        // Recent activity summary
        dashboard.put("recentLogs", dailyLogRepository.findByUserOrderByLogDateDesc(user)
            .stream().limit(7).toList());

        dashboard.put("user", convertToUserResponse(user));

        return dashboard;
    }

    public void deleteUserAccount(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(AccountStatus.DELETED);
        user.setEmail(user.getEmail() + "_DELETED_" + System.currentTimeMillis());
        userRepository.save(user);
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setAge(user.getAge());
        response.setGender(user.getGender());
        response.setOccupation(user.getOccupation());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRole(user.getRole());
        response.setEmailVerified(user.getEmailVerified());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
