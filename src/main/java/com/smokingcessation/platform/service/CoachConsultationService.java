package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.CoachConsultation;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.repository.CoachConsultationRepository;
import com.smokingcessation.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CoachConsultationService {

    private final CoachConsultationRepository consultationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<User> getAvailableCoaches() {
        return userRepository.findAllCoaches();
    }

    public List<CoachConsultation> getMemberConsultations(Long memberId) {
        return consultationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public List<CoachConsultation> getCoachConsultations(Long coachId) {
        return consultationRepository.findByCoachIdOrderByCreatedAtDesc(coachId);
    }

    public List<CoachConsultation> getPendingConsultations() {
        return consultationRepository.findPendingConsultations();
    }

    // Tạo yêu cầu tư vấn mới
    public CoachConsultation createConsultationRequest(Long memberId, Long coachId,
                                                      String subject, String message,
                                                      CoachConsultation.ConsultationType type) {
        User member = userRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thành viên"));

        User coach = userRepository.findById(coachId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy coach"));

        CoachConsultation consultation = new CoachConsultation();
        consultation.setMember(member);
        consultation.setCoach(coach);
        consultation.setSubject(subject);
        consultation.setMemberMessage(message);
        consultation.setType(type);
        consultation.setStatus(CoachConsultation.ConsultationStatus.PENDING);

        CoachConsultation saved = consultationRepository.save(consultation);

        // Gửi thông báo cho coach
        notificationService.sendCoachMessage(coachId,
            "Bạn có yêu cầu tư vấn mới từ " + member.getFullName() +
            " về chủ đề: " + subject, "Hệ thống");

        return saved;
    }

    // Coach phản hồi tư vấn
    public CoachConsultation respondToConsultation(Long consultationId, String response, Long coachId) {
        CoachConsultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu tư vấn"));

        // Kiểm tra quyền của coach
        if (!consultation.getCoach().getId().equals(coachId)) {
            throw new RuntimeException("Bạn không có quyền phản hồi tư vấn này");
        }

        consultation.setCoachResponse(response);
        consultation.setStatus(CoachConsultation.ConsultationStatus.COMPLETED);
        consultation.setRespondedAt(LocalDateTime.now());

        CoachConsultation saved = consultationRepository.save(consultation);

        // Gửi thông báo cho member
        notificationService.sendCoachMessage(consultation.getMember().getId(),
            "Coach " + consultation.getCoach().getFullName() +
            " đã phản hồi yêu cầu tư vấn của bạn về: " + consultation.getSubject(),
            consultation.getCoach().getFullName());

        return saved;
    }

    // Lên lịch tư vấn trực tiếp
    public CoachConsultation scheduleConsultation(Long consultationId, LocalDateTime scheduledTime) {
        CoachConsultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu tư vấn"));

        consultation.setScheduledTime(scheduledTime);
        consultation.setStatus(CoachConsultation.ConsultationStatus.IN_PROGRESS);

        return consultationRepository.save(consultation);
    }

    // Đánh giá và feedback cho coach
    public CoachConsultation rateConsultation(Long consultationId, Integer rating,
                                            String feedback, Long memberId) {
        CoachConsultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu tư vấn"));

        // Kiểm tra quyền của member
        if (!consultation.getMember().getId().equals(memberId)) {
            throw new RuntimeException("Bạn không có quyền đánh giá tư vấn này");
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Đánh giá phải từ 1 đến 5 sao");
        }

        consultation.setRating(rating);
        consultation.setFeedback(feedback);

        return consultationRepository.save(consultation);
    }

    // Hủy yêu cầu tư vấn
    public void cancelConsultation(Long consultationId, Long userId) {
        CoachConsultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu tư vấn"));

        // Kiểm tra quyền (member hoặc coach có thể hủy)
        if (!consultation.getMember().getId().equals(userId) &&
            !consultation.getCoach().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy tư vấn này");
        }

        consultation.setStatus(CoachConsultation.ConsultationStatus.CANCELLED);
        consultationRepository.save(consultation);
    }

    // Thống kê coach
    public CoachStats getCoachStats(Long coachId) {
        Double averageRating = consultationRepository.getAverageRatingByCoachId(coachId);
        Long completedConsultations = consultationRepository.countCompletedConsultationsByCoachId(coachId);

        return new CoachStats(
            averageRating != null ? averageRating : 0.0,
            completedConsultations
        );
    }

    public static class CoachStats {
        private final Double averageRating;
        private final Long totalConsultations;

        public CoachStats(Double averageRating, Long totalConsultations) {
            this.averageRating = averageRating;
            this.totalConsultations = totalConsultations;
        }

        public Double getAverageRating() { return averageRating; }
        public Long getTotalConsultations() { return totalConsultations; }
    }
}
