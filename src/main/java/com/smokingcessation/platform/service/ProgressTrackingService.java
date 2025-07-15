package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.ProgressTracking;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.SmokingStatus;
import com.smokingcessation.platform.repository.ProgressTrackingRepository;
import com.smokingcessation.platform.repository.SmokingStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressTrackingService {

    private final ProgressTrackingRepository progressTrackingRepository;
    private final SmokingStatusRepository smokingStatusRepository;

    public ProgressTracking recordDailyProgress(Long userId, ProgressTracking progress) {
        User user = new User();
        user.setId(userId);
        progress.setUser(user);

        // Tính toán các chỉ số tiến trình
        calculateProgressMetrics(progress);

        return progressTrackingRepository.save(progress);
    }

    // Ghi nhận sự kiện hút thuốc
    public ProgressTracking recordSmokingEvent(Long userId, int cigarettesSmoked) {
        LocalDate today = LocalDate.now();
        ProgressTracking progress = progressTrackingRepository
            .findByUserIdAndTrackingDate(userId, today)
            .orElse(new ProgressTracking());

        if (progress.getId() == null) {
            User user = new User();
            user.setId(userId);
            progress.setUser(user);
            progress.setTrackingDate(today);
            progress.setCigarettesSmoked(0);
        }

        progress.setCigarettesSmoked(progress.getCigarettesSmoked() + cigarettesSmoked);

        // Reset chuỗi ngày không hút nếu đã hút thuốc
        if (cigarettesSmoked > 0) {
            progress.setCurrentStreak(0);
        }

        calculateProgressMetrics(progress);

        return progressTrackingRepository.save(progress);
    }

    private void calculateProgressMetrics(ProgressTracking progress) {
        Long userId = progress.getUser().getId();

        // Tính tiền tiết kiệm
        Optional<SmokingStatus> smokingStatusOpt = smokingStatusRepository.findByUserId(userId);
        if (smokingStatusOpt.isPresent()) {
            SmokingStatus status = smokingStatusOpt.get();
            BigDecimal dailyCost = status.getCigarettePrice()
                .multiply(BigDecimal.valueOf(status.getCigarettesPerDay()));

            // Tiền tiết kiệm hôm nay = chi phí hàng ngày - (số điếu đã hút * giá per điếu)
            BigDecimal pricePerCigarette = status.getCigarettePrice()
                .divide(BigDecimal.valueOf(20), 2, BigDecimal.ROUND_HALF_UP); // giả sử 20 điếu/bao
            BigDecimal moneySpent = pricePerCigarette.multiply(BigDecimal.valueOf(progress.getCigarettesSmoked()));
            BigDecimal moneySaved = dailyCost.subtract(moneySpent);

            progress.setMoneySaved(moneySaved.max(BigDecimal.ZERO));
        }

        // Cập nhật chuỗi ngày không hút
        if (progress.getCigarettesSmoked() == 0) {
            Integer previousStreak = getPreviousStreak(userId, progress.getTrackingDate());
            progress.setCurrentStreak(previousStreak + 1);
            progress.setDaysSmokeFreePeak(Math.max(progress.getDaysSmokeFreePeak(), progress.getCurrentStreak()));
        }
    }

    private Integer getPreviousStreak(Long userId, LocalDate currentDate) {
        LocalDate previousDate = currentDate.minusDays(1);
        return progressTrackingRepository.findByUserIdAndTrackingDate(userId, previousDate)
            .map(ProgressTracking::getCurrentStreak)
            .orElse(0);
    }

    public List<ProgressTracking> getUserProgress(Long userId) {
        return progressTrackingRepository.findByUserIdOrderByTrackingDateDesc(userId);
    }

    public List<ProgressTracking> getUserProgressInRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return progressTrackingRepository.findByUserIdAndDateRange(userId, startDate, endDate);
    }

    public Optional<ProgressTracking> getTodayProgress(Long userId) {
        return progressTrackingRepository.findByUserIdAndTrackingDate(userId, LocalDate.now());
    }

    // Tính toán thống kê tổng thể của user
    public ProgressStats calculateUserStats(Long userId) {
        List<ProgressTracking> allProgress = progressTrackingRepository.findByUserId(userId);

        int maxStreak = progressTrackingRepository.findMaxStreakByUserId(userId).orElse(0);

        BigDecimal totalMoneySaved = allProgress.stream()
            .map(ProgressTracking::getMoneySaved)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalDaysTracked = allProgress.size();
        int currentStreak = allProgress.isEmpty() ? 0 : allProgress.get(0).getCurrentStreak();

        // Tính tổng số ngày không hút thuốc
        long totalSmokeFreeeDays = allProgress.stream()
            .mapToLong(p -> p.getCigarettesSmoked() == 0 ? 1 : 0)
            .sum();

        return new ProgressStats(maxStreak, totalMoneySaved, totalDaysTracked, currentStreak, totalSmokeFreeeDays);
    }

    public static class ProgressStats {
        private final int maxStreak;
        private final BigDecimal totalMoneySaved;
        private final long totalDaysTracked;
        private final int currentStreak;
        private final long totalSmokeFreeeDays;

        public ProgressStats(int maxStreak, BigDecimal totalMoneySaved, long totalDaysTracked,
                           int currentStreak, long totalSmokeFreeeDays) {
            this.maxStreak = maxStreak;
            this.totalMoneySaved = totalMoneySaved;
            this.totalDaysTracked = totalDaysTracked;
            this.currentStreak = currentStreak;
            this.totalSmokeFreeeDays = totalSmokeFreeeDays;
        }

        // Getters
        public int getMaxStreak() { return maxStreak; }
        public BigDecimal getTotalMoneySaved() { return totalMoneySaved; }
        public long getTotalDaysTracked() { return totalDaysTracked; }
        public int getCurrentStreak() { return currentStreak; }
        public long getTotalSmokeFreeeDays() { return totalSmokeFreeeDays; }
    }
}
