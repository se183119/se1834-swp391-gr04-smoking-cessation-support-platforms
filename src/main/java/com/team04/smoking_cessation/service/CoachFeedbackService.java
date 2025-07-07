package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.CoachFeedback;
import com.team04.smoking_cessation.entity.CoachSession;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.repository.CoachFeedbackRepository;
import com.team04.smoking_cessation.repository.CoachSessionRepository;
import com.team04.smoking_cessation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CoachFeedbackService {
    @Autowired
    private CoachFeedbackRepository coachFeedbackRepository;
    @Autowired
    private CoachSessionRepository coachSessionRepository;
    @Autowired
    private UserRepository userRepository;

    // Member gửi feedback cho session
    public CoachFeedback submitFeedback(Long sessionId, String memberEmail, int rating, String content) {
        CoachSession session = coachSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        User member = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        User coach = session.getCoach();
        CoachFeedback feedback = new CoachFeedback();
        feedback.setSession(session);
        feedback.setMember(member);
        feedback.setCoach(coach);
        feedback.setRating(rating);
        feedback.setContent(content);
        feedback.setCreatedAt(LocalDateTime.now());
        return coachFeedbackRepository.save(feedback);
    }

    // Lấy feedback của session
    public List<CoachFeedback> getFeedbacksForSession(Long sessionId) {
        CoachSession session = coachSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return coachFeedbackRepository.findBySession(session);
    }

    // Lấy feedback của coach
    public List<CoachFeedback> getFeedbacksForCoach(Long coachId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found"));
        return coachFeedbackRepository.findByCoach(coach);
    }

    // Lấy feedback của member
    public List<CoachFeedback> getFeedbacksForMember(Long memberId) {
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return coachFeedbackRepository.findByMember(member);
    }
} 