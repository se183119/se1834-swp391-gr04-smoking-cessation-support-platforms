package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.CoachConsultation;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.service.CoachConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CoachConsultationController {

    private final CoachConsultationService consultationService;

    // Lấy danh sách coaches có sẵn
    @GetMapping("/coaches")
    public ResponseEntity<List<User>> getAvailableCoaches() {
        List<User> coaches = consultationService.getAvailableCoaches();
        return ResponseEntity.ok(coaches);
    }

    // Lấy danh sách tư vấn của member
    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<CoachConsultation>> getMemberConsultations(@PathVariable Long memberId) {
        List<CoachConsultation> consultations = consultationService.getMemberConsultations(memberId);
        return ResponseEntity.ok(consultations);
    }

    // Lấy danh sách tư vấn của coach
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<CoachConsultation>> getCoachConsultations(@PathVariable Long coachId) {
        List<CoachConsultation> consultations = consultationService.getCoachConsultations(coachId);
        return ResponseEntity.ok(consultations);
    }

    // Lấy danh sách tư vấn đang chờ xử lý
    @GetMapping("/pending")
    public ResponseEntity<List<CoachConsultation>> getPendingConsultations() {
        List<CoachConsultation> pendingConsultations = consultationService.getPendingConsultations();
        return ResponseEntity.ok(pendingConsultations);
    }

    // Tạo yêu cầu tư vấn mới
    @PostMapping
    public ResponseEntity<CoachConsultation> createConsultationRequest(@RequestBody ConsultationRequest request) {
        try {
            CoachConsultation consultation = consultationService.createConsultationRequest(
                request.getMemberId(), request.getCoachId(), request.getSubject(),
                request.getMessage(), request.getType());
            return ResponseEntity.ok(consultation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Coach phản hồi tư vấn
    @PostMapping("/{consultationId}/respond")
    public ResponseEntity<CoachConsultation> respondToConsultation(@PathVariable Long consultationId,
                                                                 @RequestBody ConsultationResponse request) {
        try {
            CoachConsultation consultation = consultationService.respondToConsultation(
                consultationId, request.getResponse(), request.getCoachId());
            return ResponseEntity.ok(consultation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lên lịch tư vấn trực tiếp
    @PostMapping("/{consultationId}/schedule")
    public ResponseEntity<CoachConsultation> scheduleConsultation(@PathVariable Long consultationId,
                                                                @RequestBody ScheduleRequest request) {
        try {
            CoachConsultation consultation = consultationService.scheduleConsultation(
                consultationId, request.getScheduledTime());
            return ResponseEntity.ok(consultation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Đánh giá và feedback cho coach
    @PostMapping("/{consultationId}/rate")
    public ResponseEntity<CoachConsultation> rateConsultation(@PathVariable Long consultationId,
                                                            @RequestBody RatingRequest request) {
        try {
            CoachConsultation consultation = consultationService.rateConsultation(
                consultationId, request.getRating(), request.getFeedback(), request.getMemberId());
            return ResponseEntity.ok(consultation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Hủy yêu cầu tư vấn
    @PostMapping("/{consultationId}/cancel")
    public ResponseEntity<Void> cancelConsultation(@PathVariable Long consultationId,
                                                  @RequestBody CancelRequest request) {
        try {
            consultationService.cancelConsultation(consultationId, request.getUserId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy thống kê coach
    @GetMapping("/coach/{coachId}/stats")
    public ResponseEntity<CoachConsultationService.CoachStats> getCoachStats(@PathVariable Long coachId) {
        CoachConsultationService.CoachStats stats = consultationService.getCoachStats(coachId);
        return ResponseEntity.ok(stats);
    }

    // DTOs
    public static class ConsultationRequest {
        private Long memberId;
        private Long coachId;
        private String subject;
        private String message;
        private CoachConsultation.ConsultationType type;

        // Getters and setters
        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }
        public Long getCoachId() { return coachId; }
        public void setCoachId(Long coachId) { this.coachId = coachId; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public CoachConsultation.ConsultationType getType() { return type; }
        public void setType(CoachConsultation.ConsultationType type) { this.type = type; }
    }

    public static class ConsultationResponse {
        private String response;
        private Long coachId;

        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
        public Long getCoachId() { return coachId; }
        public void setCoachId(Long coachId) { this.coachId = coachId; }
    }

    public static class ScheduleRequest {
        private LocalDateTime scheduledTime;

        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    }

    public static class RatingRequest {
        private Integer rating;
        private String feedback;
        private Long memberId;

        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }
    }

    public static class CancelRequest {
        private Long userId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }
}
