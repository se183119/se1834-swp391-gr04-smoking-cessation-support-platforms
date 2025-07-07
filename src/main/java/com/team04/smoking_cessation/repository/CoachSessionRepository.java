package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.CoachSession;
import com.team04.smoking_cessation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoachSessionRepository extends JpaRepository<CoachSession, Long> {
    List<CoachSession> findByMember(User member);
    List<CoachSession> findByCoach(User coach);
    List<CoachSession> findByMemberAndStartTimeAfter(User member, LocalDateTime time);
    List<CoachSession> findByCoachAndStartTimeAfter(User coach, LocalDateTime time);
} 