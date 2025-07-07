package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.CoachFeedback;
import com.team04.smoking_cessation.entity.CoachSession;
import com.team04.smoking_cessation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachFeedbackRepository extends JpaRepository<CoachFeedback, Long> {
    List<CoachFeedback> findBySession(CoachSession session);
    List<CoachFeedback> findByCoach(User coach);
    List<CoachFeedback> findByMember(User member);
} 