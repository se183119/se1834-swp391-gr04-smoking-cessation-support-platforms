package com.smokingcessation.platform.repository;

import com.smokingcessation.platform.entity.CoachConsultation;
import com.smokingcessation.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachConsultationRepository extends JpaRepository<CoachConsultation, Long> {

    List<CoachConsultation> findByMember(User member);

    List<CoachConsultation> findByCoach(User coach);

    List<CoachConsultation> findByMemberId(Long memberId);

    List<CoachConsultation> findByCoachId(Long coachId);

    List<CoachConsultation> findByStatus(CoachConsultation.ConsultationStatus status);

    @Query("SELECT cc FROM CoachConsultation cc WHERE cc.member.id = :memberId ORDER BY cc.createdAt DESC")
    List<CoachConsultation> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId);

    @Query("SELECT cc FROM CoachConsultation cc WHERE cc.coach.id = :coachId ORDER BY cc.createdAt DESC")
    List<CoachConsultation> findByCoachIdOrderByCreatedAtDesc(@Param("coachId") Long coachId);

    @Query("SELECT cc FROM CoachConsultation cc WHERE cc.status = 'PENDING' ORDER BY cc.createdAt ASC")
    List<CoachConsultation> findPendingConsultations();

    @Query("SELECT AVG(cc.rating) FROM CoachConsultation cc WHERE cc.coach.id = :coachId AND cc.rating IS NOT NULL")
    Double getAverageRatingByCoachId(@Param("coachId") Long coachId);

    @Query("SELECT COUNT(cc) FROM CoachConsultation cc WHERE cc.coach.id = :coachId AND cc.status = 'COMPLETED'")
    Long countCompletedConsultationsByCoachId(@Param("coachId") Long coachId);
}
