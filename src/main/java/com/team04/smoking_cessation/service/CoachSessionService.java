package com.team04.smoking_cessation.service;

import com.team04.smoking_cessation.entity.CoachSession;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.repository.CoachSessionRepository;
import com.team04.smoking_cessation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CoachSessionService {
    @Autowired
    private CoachSessionRepository coachSessionRepository;
    @Autowired
    private UserRepository userRepository;

    // Member đặt lịch hẹn với coach
    public CoachSession bookSession(String memberEmail, Long coachId, LocalDateTime startTime, LocalDateTime endTime, String note) {
        User member = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found"));
        CoachSession session = new CoachSession();
        session.setMember(member);
        session.setCoach(coach);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setNote(note);
        session.setStatus(CoachSession.SessionStatus.PENDING);
        return coachSessionRepository.save(session);
    }

    // Coach xác nhận lịch hẹn
    public CoachSession confirmSession(Long sessionId, String coachEmail) {
        CoachSession session = coachSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getCoach().getEmail().equals(coachEmail)) {
            throw new RuntimeException("Access denied");
        }
        session.setStatus(CoachSession.SessionStatus.CONFIRMED);
        return coachSessionRepository.save(session);
    }

    // Lấy danh sách lịch hẹn của member
    public List<CoachSession> getSessionsForMember(String memberEmail) {
        User member = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        return coachSessionRepository.findByMember(member);
    }

    // Lấy danh sách lịch hẹn của coach
    public List<CoachSession> getSessionsForCoach(String coachEmail) {
        User coach = userRepository.findByEmail(coachEmail)
                .orElseThrow(() -> new RuntimeException("Coach not found"));
        return coachSessionRepository.findByCoach(coach);
    }

    // Coach/Member hủy lịch hẹn
    public CoachSession cancelSession(Long sessionId, String userEmail) {
        CoachSession session = coachSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getCoach().getEmail().equals(userEmail) && !session.getMember().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied");
        }
        session.setStatus(CoachSession.SessionStatus.CANCELLED);
        return coachSessionRepository.save(session);
    }

    // Đánh dấu hoàn thành
    public CoachSession completeSession(Long sessionId, String coachEmail) {
        CoachSession session = coachSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getCoach().getEmail().equals(coachEmail)) {
            throw new RuntimeException("Access denied");
        }
        session.setStatus(CoachSession.SessionStatus.COMPLETED);
        return coachSessionRepository.save(session);
    }
} 