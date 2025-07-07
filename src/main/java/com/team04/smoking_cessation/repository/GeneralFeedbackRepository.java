package com.team04.smoking_cessation.repository;

import com.team04.smoking_cessation.entity.GeneralFeedback;
import com.team04.smoking_cessation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneralFeedbackRepository extends JpaRepository<GeneralFeedback, Long> {
    List<GeneralFeedback> findByType(GeneralFeedback.FeedbackType type);
    List<GeneralFeedback> findByTargetIdAndType(Long targetId, GeneralFeedback.FeedbackType type);
    List<GeneralFeedback> findByUser(User user);
    List<GeneralFeedback> findByStatus(GeneralFeedback.FeedbackStatus status);
} 