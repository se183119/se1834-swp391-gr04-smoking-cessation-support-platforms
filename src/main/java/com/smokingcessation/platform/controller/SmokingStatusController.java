package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.SmokingStatus;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.service.SmokingStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/smoking-status")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SmokingStatusController {

    private final SmokingStatusService smokingStatusService;

    // Tạo hoặc cập nhật thông tin hút thuốc
    @PostMapping("/{userId}")
    public ResponseEntity<SmokingStatus> createOrUpdateSmokingStatus(@PathVariable Long userId,
                                                                   @RequestBody SmokingStatusRequest request) {
        try {
            SmokingStatus smokingStatus = new SmokingStatus();
            User user = new User();
            user.setId(userId);
            smokingStatus.setUser(user);
            smokingStatus.setCigarettesPerDay(request.getCigarettesPerDay());
            smokingStatus.setSmokingFrequency(request.getSmokingFrequency());
            smokingStatus.setCigarettePrice(request.getCigarettePrice());
            smokingStatus.setBrandName(request.getBrandName());
            smokingStatus.setYearsSmoking(request.getYearsSmoking());
            smokingStatus.setAttemptsToQuit(request.getAttemptsToQuit());
            smokingStatus.setTriggers(request.getTriggers());
            smokingStatus.setMotivationLevel(request.getMotivationLevel());

            SmokingStatus saved = smokingStatusService.createOrUpdateSmokingStatus(smokingStatus);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Lấy thông tin hút thuốc hiện tại của user
    @GetMapping("/{userId}/current")
    public ResponseEntity<SmokingStatus> getCurrentSmokingStatus(@PathVariable Long userId) {
        try {
            SmokingStatus currentStatus = smokingStatusService.findCurrentSmokingStatus(userId);
            return ResponseEntity.ok(currentStatus);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Lấy thông tin hút thuốc của user
    @GetMapping("/{userId}")
    public ResponseEntity<List<SmokingStatus>> getSmokingStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(smokingStatusService.findByUserId(userId));
    }

    // Cập nhật thói quen hút thuốc
    @PutMapping("/{userId}/habits")
    public ResponseEntity<SmokingStatus> updateSmokingHabits(@PathVariable Long userId,
                                                           @RequestBody SmokingHabitsRequest request) {
        try {
            SmokingStatus updated = smokingStatusService.updateSmokingHabits(userId,
                request.getCigarettesPerDay(), request.getFrequency(),
                request.getBrandName(), request.getCigarettePrice());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Cập nhật lịch sử hút thuốc
    @PutMapping("/{userId}/history")
    public ResponseEntity<SmokingStatus> updateSmokingHistory(@PathVariable Long userId,
                                                            @RequestBody SmokingHistoryRequest request) {
        try {
            SmokingStatus updated = smokingStatusService.recordSmokingHistory(userId,
                request.getYearsSmoking(), request.getAttemptsToQuit(),
                request.getTriggers(), request.getMotivationLevel());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Kiểm tra user có thông tin hút thuốc không
    @GetMapping("/{userId}/exists")
    public ResponseEntity<Boolean> hasSmokingStatus(@PathVariable Long userId) {
        boolean exists = smokingStatusService.hasSmokingStatus(userId);
        return ResponseEntity.ok(exists);
    }

    // Xóa thông tin hút thuốc
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteSmokingStatus(@PathVariable Long userId) {
        try {
            smokingStatusService.deleteSmokingStatus(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // DTOs
    public static class SmokingStatusRequest {
        private Integer cigarettesPerDay;
        private String smokingFrequency;
        private BigDecimal cigarettePrice;
        private String brandName;
        private Integer yearsSmoking;
        private Integer attemptsToQuit;
        private String triggers;
        private SmokingStatus.MotivationLevel motivationLevel;

        // Getters and setters
        public Integer getCigarettesPerDay() { return cigarettesPerDay; }
        public void setCigarettesPerDay(Integer cigarettesPerDay) { this.cigarettesPerDay = cigarettesPerDay; }
        public String getSmokingFrequency() { return smokingFrequency; }
        public void setSmokingFrequency(String smokingFrequency) { this.smokingFrequency = smokingFrequency; }
        public BigDecimal getCigarettePrice() { return cigarettePrice; }
        public void setCigarettePrice(BigDecimal cigarettePrice) { this.cigarettePrice = cigarettePrice; }
        public String getBrandName() { return brandName; }
        public void setBrandName(String brandName) { this.brandName = brandName; }
        public Integer getYearsSmoking() { return yearsSmoking; }
        public void setYearsSmoking(Integer yearsSmoking) { this.yearsSmoking = yearsSmoking; }
        public Integer getAttemptsToQuit() { return attemptsToQuit; }
        public void setAttemptsToQuit(Integer attemptsToQuit) { this.attemptsToQuit = attemptsToQuit; }
        public String getTriggers() { return triggers; }
        public void setTriggers(String triggers) { this.triggers = triggers; }
        public SmokingStatus.MotivationLevel getMotivationLevel() { return motivationLevel; }
        public void setMotivationLevel(SmokingStatus.MotivationLevel motivationLevel) { this.motivationLevel = motivationLevel; }
    }

    public static class SmokingHabitsRequest {
        private Integer cigarettesPerDay;
        private String frequency;
        private String brandName;
        private BigDecimal cigarettePrice;

        // Getters and setters
        public Integer getCigarettesPerDay() { return cigarettesPerDay; }
        public void setCigarettesPerDay(Integer cigarettesPerDay) { this.cigarettesPerDay = cigarettesPerDay; }
        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }
        public String getBrandName() { return brandName; }
        public void setBrandName(String brandName) { this.brandName = brandName; }
        public BigDecimal getCigarettePrice() { return cigarettePrice; }
        public void setCigarettePrice(BigDecimal cigarettePrice) { this.cigarettePrice = cigarettePrice; }
    }

    public static class SmokingHistoryRequest {
        private Integer yearsSmoking;
        private Integer attemptsToQuit;
        private String triggers;
        private SmokingStatus.MotivationLevel motivationLevel;

        // Getters and setters
        public Integer getYearsSmoking() { return yearsSmoking; }
        public void setYearsSmoking(Integer yearsSmoking) { this.yearsSmoking = yearsSmoking; }
        public Integer getAttemptsToQuit() { return attemptsToQuit; }
        public void setAttemptsToQuit(Integer attemptsToQuit) { this.attemptsToQuit = attemptsToQuit; }
        public String getTriggers() { return triggers; }
        public void setTriggers(String triggers) { this.triggers = triggers; }
        public SmokingStatus.MotivationLevel getMotivationLevel() { return motivationLevel; }
        public void setMotivationLevel(SmokingStatus.MotivationLevel motivationLevel) { this.motivationLevel = motivationLevel; }
    }
}
