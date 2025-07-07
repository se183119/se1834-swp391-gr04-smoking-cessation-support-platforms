package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.GeneralFeedback;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.repository.GeneralFeedbackRepository;
import com.team04.smoking_cessation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GeneralFeedbackService {
    @Autowired
    private GeneralFeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;

    // Member gửi feedback
    public GeneralFeedback submitFeedback(String userEmail, GeneralFeedback.FeedbackType type, Long targetId, int rating, String content) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        GeneralFeedback feedback = new GeneralFeedback();
        feedback.setUser(user);
        feedback.setType(type);
        feedback.setTargetId(targetId);
        feedback.setRating(rating);
        feedback.setContent(content);
        feedback.setStatus(GeneralFeedback.FeedbackStatus.PENDING);
        feedback.setCreatedAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    // Lấy feedback theo loại
    public List<GeneralFeedback> getFeedbacksByType(GeneralFeedback.FeedbackType type) {
        return feedbackRepository.findByType(type);
    }

    // Lấy feedback cho 1 target cụ thể (ví dụ: 1 bài viết)
    public List<GeneralFeedback> getFeedbacksForTarget(Long targetId, GeneralFeedback.FeedbackType type) {
        return feedbackRepository.findByTargetIdAndType(targetId, type);
    }

    // Lấy feedback của 1 user
    public List<GeneralFeedback> getFeedbacksByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return feedbackRepository.findByUser(user);
    }

    // Lấy feedback theo trạng thái (cho admin duyệt)
    public List<GeneralFeedback> getFeedbacksByStatus(GeneralFeedback.FeedbackStatus status) {
        return feedbackRepository.findByStatus(status);
    }

    // Admin duyệt feedback
    public GeneralFeedback approveFeedback(Long feedbackId) {
        GeneralFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setStatus(GeneralFeedback.FeedbackStatus.APPROVED);
        return feedbackRepository.save(feedback);
    }
    public GeneralFeedback rejectFeedback(Long feedbackId) {
        GeneralFeedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
        feedback.setStatus(GeneralFeedback.FeedbackStatus.REJECTED);
        return feedbackRepository.save(feedback);
    }
} 