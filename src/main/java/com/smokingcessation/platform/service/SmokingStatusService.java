package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.SmokingStatus;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.repository.SmokingStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SmokingStatusService {

    private final SmokingStatusRepository smokingStatusRepository;

    public SmokingStatus createOrUpdateSmokingStatus(SmokingStatus smokingStatus) {
//        var existingStatus = smokingStatusRepository.findByUserId(smokingStatus.getUser().getId());
//        if (existingStatus.isPresent()) {
//            // Update existing smoking status
//            SmokingStatus existing = existingStatus.get();
//            existing.setCigarettesPerDay(smokingStatus.getCigarettesPerDay());
//            existing.setSmokingFrequency(smokingStatus.getSmokingFrequency());
//            existing.setBrandName(smokingStatus.getBrandName());
//            existing.setCigarettePrice(smokingStatus.getCigarettePrice());
//            existing.setYearsSmoking(smokingStatus.getYearsSmoking());
//            existing.setAttemptsToQuit(smokingStatus.getAttemptsToQuit());
//            existing.setTriggers(smokingStatus.getTriggers());
//            existing.setMotivationLevel(smokingStatus.getMotivationLevel());
//            return smokingStatusRepository.save(existing);
//        }

        return smokingStatusRepository.save(smokingStatus);
    }

    public List<SmokingStatus> findByUserId(Long userId) {
        return smokingStatusRepository.findAllByUserId(userId);
    }

    public SmokingStatus findCurrentSmokingStatus(Long userId) {
        List<SmokingStatus> statuses = smokingStatusRepository.findAllByUserId(userId);
        if (statuses.isEmpty()) {
            throw new RuntimeException("Không tìm thấy thông tin hút thuốc cho người dùng với ID: " + userId);
        }
        return statuses.get(statuses.size() - 1);
    }

    public Optional<SmokingStatus> findByUser(User user) {
        return smokingStatusRepository.findByUser(user);
    }

    public boolean hasSmokingStatus(Long userId) {
        return smokingStatusRepository.existsByUserId(userId);
    }

    public SmokingStatus updateSmokingHabits(Long userId, Integer cigarettesPerDay,
                                           String frequency, String brandName,
                                           java.math.BigDecimal cigarettePrice) {
        SmokingStatus status = smokingStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin hút thuốc"));

        status.setCigarettesPerDay(cigarettesPerDay);
        status.setSmokingFrequency(frequency);
        status.setBrandName(brandName);
        status.setCigarettePrice(cigarettePrice);

        return smokingStatusRepository.save(status);
    }

    public SmokingStatus recordSmokingHistory(Long userId, Integer yearsSmoking,
                                            Integer attemptsToQuit, String triggers,
                                            SmokingStatus.MotivationLevel motivationLevel) {
        SmokingStatus status = smokingStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin hút thuốc"));

        status.setYearsSmoking(yearsSmoking);
        status.setAttemptsToQuit(attemptsToQuit);
        status.setTriggers(triggers);
        status.setMotivationLevel(motivationLevel);

        return smokingStatusRepository.save(status);
    }

    public void deleteSmokingStatus(Long userId) {
        smokingStatusRepository.findByUserId(userId)
            .ifPresent(smokingStatusRepository::delete);
    }
}
