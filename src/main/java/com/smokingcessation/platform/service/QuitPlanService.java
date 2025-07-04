package com.smokingcessation.platform.service;

import com.smokingcessation.platform.entity.QuitPlan;
import com.smokingcessation.platform.entity.QuitPlanPhase;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.SmokingStatus;
import com.smokingcessation.platform.repository.QuitPlanRepository;
import com.smokingcessation.platform.repository.SmokingStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class QuitPlanService {

    private final QuitPlanRepository quitPlanRepository;
    private final SmokingStatusRepository smokingStatusRepository;

    public QuitPlan createQuitPlan(QuitPlan quitPlan) {
        return quitPlanRepository.save(quitPlan);
    }

    // Hệ thống tự động tạo kế hoạch cai thuốc dựa trên thông tin người dùng
    public QuitPlan generateSystemQuitPlan(User user, String quitReason, LocalDate startDate, LocalDate targetDate) {
        Optional<SmokingStatus> smokingStatusOpt = smokingStatusRepository.findByUser(user);

        QuitPlan quitPlan = new QuitPlan();
        quitPlan.setUser(user);
        quitPlan.setTitle("Kế hoạch cai thuốc do hệ thống tạo");
        quitPlan.setQuitReason(quitReason);
        quitPlan.setStartDate(startDate);
        quitPlan.setTargetQuitDate(targetDate);
        quitPlan.setQuitMethod(QuitPlan.QuitMethod.GRADUAL_REDUCTION);
        quitPlan.setIsSystemGenerated(true);

        // Tạo các giai đoạn dựa trên thông tin hút thuốc hiện tại
        List<QuitPlanPhase> phases = generateQuitPhases(quitPlan, smokingStatusOpt.orElse(null));
        quitPlan.setPhases(phases);

        return quitPlanRepository.save(quitPlan);
    }

    private List<QuitPlanPhase> generateQuitPhases(QuitPlan quitPlan, SmokingStatus smokingStatus) {
        List<QuitPlanPhase> phases = new ArrayList<>();

        if (smokingStatus != null) {
            int currentCigarettes = smokingStatus.getCigarettesPerDay();
            LocalDate startDate = quitPlan.getStartDate();

            // Giai đoạn 1: Giảm 50%
            QuitPlanPhase phase1 = new QuitPlanPhase();
            phase1.setQuitPlan(quitPlan);
            phase1.setTitle("Giai đoạn 1: Giảm ban đầu");
            phase1.setDescription("Giảm 50% số lượng thuốc hút hàng ngày");
            phase1.setPhaseNumber(1);
            phase1.setStartDate(startDate);
            phase1.setEndDate(startDate.plusWeeks(2));
            phase1.setTargetCigarettesPerDay(currentCigarettes / 2);
            phase1.setGoals("Giảm từ " + currentCigarettes + " xuống " + (currentCigarettes / 2) + " điếu/ngày");
            phases.add(phase1);

            // Giai đoạn 2: Giảm xuống 25%
            QuitPlanPhase phase2 = new QuitPlanPhase();
            phase2.setQuitPlan(quitPlan);
            phase2.setTitle("Giai đoạn 2: Giảm sâu");
            phase2.setDescription("Giảm xuống 25% so với ban đầu");
            phase2.setPhaseNumber(2);
            phase2.setStartDate(startDate.plusWeeks(2));
            phase2.setEndDate(startDate.plusWeeks(4));
            phase2.setTargetCigarettesPerDay(Math.max(1, currentCigarettes / 4));
            phase2.setGoals("Giảm xuống còn " + Math.max(1, currentCigarettes / 4) + " điếu/ngày");
            phases.add(phase2);

            // Giai đoạn 3: Cai hoàn toàn
            QuitPlanPhase phase3 = new QuitPlanPhase();
            phase3.setQuitPlan(quitPlan);
            phase3.setTitle("Giai đoạn 3: Cai hoàn toàn");
            phase3.setDescription("Ngưng hút thuốc hoàn toàn");
            phase3.setPhaseNumber(3);
            phase3.setStartDate(startDate.plusWeeks(4));
            phase3.setEndDate(quitPlan.getTargetQuitDate());
            phase3.setTargetCigarettesPerDay(0);
            phase3.setGoals("Không hút thuốc hoàn toàn");
            phases.add(phase3);
        }

        return phases;
    }

    public List<QuitPlan> findUserQuitPlans(Long userId) {
        return quitPlanRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<QuitPlan> findActiveQuitPlan(Long userId) {
        return quitPlanRepository.findActiveQuitPlanByUserId(userId, QuitPlan.PlanStatus.ACTIVE);
    }

    public QuitPlan updateQuitPlan(QuitPlan quitPlan) {
        return quitPlanRepository.save(quitPlan);
    }

    public QuitPlan customizeQuitPlan(Long planId, String customizationNotes) {
        QuitPlan plan = quitPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch cai thuốc"));
        plan.setCustomizationNotes(customizationNotes);
        return quitPlanRepository.save(plan);
    }

    public void updatePlanStatus(Long planId, QuitPlan.PlanStatus status) {
        QuitPlan plan = quitPlanRepository.findById(planId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch cai thuốc"));
        plan.setStatus(status);
        quitPlanRepository.save(plan);
    }
}
