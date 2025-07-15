package com.smokingcessation.platform.service;

import com.smokingcessation.platform.dto.*;
import com.smokingcessation.platform.entity.*;
import com.smokingcessation.platform.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class QuitPlanService {
    private final QuitPlanRepository planRepo;
    private final PlanMilestoneRepository milestoneRepo;
    private final UserRepository userRepo;
    private final UserProgressRepository progressRepo;

    @Transactional
    public PlanResponse createPlan(CreatePlanRequest req) {
        User user = userRepo.findById(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        QuitPlan plan = new QuitPlan();
        plan.setUser(user);
        plan.setStartDate(LocalDate.now());
        plan.setQuitMonths(req.getQuitMonths());
        plan = planRepo.save(plan);

        // Tính milestones theo stair-step
        int N0 = req.getCigsPerDay();
        int totalDays = req.getQuitMonths().multiply(BigDecimal.valueOf(30)).intValue();
        double D = (double) totalDays / N0;

        QuitPlan savedPlan = planRepo.save(plan);
        List<PlanMilestone> list = IntStream.rangeClosed(1, N0)
                .mapToObj(k -> {
                    PlanMilestone m = new PlanMilestone();
                    m.setQuitPlan(savedPlan);
                    m.setStepIndex(k);
                    m.setDayOffset((int) Math.ceil(k * D));
                    m.setTargetCigarettes(N0 - k);
                    return m;
                })
                .collect(Collectors.toList());

        milestoneRepo.saveAll(list);

        // Trả về DTO
        PlanResponse resp = new PlanResponse();
        resp.setPlanId(plan.getId());
        resp.setStartDate(plan.getStartDate());
        resp.setQuitMonths(plan.getQuitMonths());
        resp.setMilestones(list.stream().map(m -> {
            MilestoneDto d = new MilestoneDto();
            d.setStepIndex(m.getStepIndex());
            d.setDayOffset(m.getDayOffset());
            d.setTargetCigarettes(m.getTargetCigarettes());
            return d;
        }).collect(Collectors.toList()));
        return resp;
    }

    @Transactional(readOnly = true)
    public PlanResponse getPlanById(Long planId) {
        QuitPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found id=" + planId));

        // Lấy list milestone đã order by stepIndex
        List<PlanMilestone> milestones = milestoneRepo.findByQuitPlanIdOrderByStepIndex(planId);

        // Map xuống DTO
        List<MilestoneDto> dtoList = milestones.stream()
                .map(m -> {
                    MilestoneDto d = new MilestoneDto();
                    d.setStepIndex(m.getStepIndex());
                    d.setDayOffset(m.getDayOffset());
                    d.setTargetCigarettes(m.getTargetCigarettes());
                    return d;
                })
                .collect(Collectors.toList());

        // Build response
        PlanResponse resp = new PlanResponse();
        resp.setPlanId(plan.getId());
        resp.setStartDate(plan.getStartDate());
        resp.setQuitMonths(plan.getQuitMonths());
        resp.setMilestones(dtoList);

        return resp;
    }

    @Transactional
    public ProgressResponse upsertProgress(Long planId, ProgressRequest req) {
        QuitPlan plan = planRepo.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found"));

        UserProgress up = progressRepo.findByQuitPlanIdAndLogDate(planId, req.getLogDate())
                .orElseGet(() -> {
                    UserProgress np = new UserProgress();
                    np.setQuitPlan(plan);
                    np.setLogDate(req.getLogDate());
                    return np;
                });
        up.setSmoked(req.getSmoked());
        up.setNote(req.getNote());
        up = progressRepo.save(up);

        ProgressResponse resp = new ProgressResponse();
        resp.setLogDate(up.getLogDate());
        resp.setSmoked(up.getSmoked());
        resp.setNote(up.getNote());
        return resp;
    }

    public List<ProgressResponse> getProgressInMonth(Long planId, YearMonth ym) {
        LocalDate from = ym.atDay(1);
        LocalDate to   = ym.atEndOfMonth();
        return progressRepo.findByQuitPlanIdAndLogDateBetween(planId, from, to)
                .stream()
                .map(up -> {
                    ProgressResponse p = new ProgressResponse();
                    p.setLogDate(up.getLogDate());
                    p.setSmoked(up.getSmoked());
                    p.setNote(up.getNote());
                    return p;
                }).collect(Collectors.toList());
    }

    public List<PlanResponse> getByUser(Long userId){
        return planRepo.findByUserId(userId)
                .stream()
                .map(plan -> {
                    PlanResponse resp = new PlanResponse();
                    resp.setPlanId(plan.getId());
                    resp.setStartDate(plan.getStartDate());
                    resp.setQuitMonths(plan.getQuitMonths());

                    List<PlanMilestone> milestones = milestoneRepo.findByQuitPlanIdOrderByStepIndex(plan.getId());
                    resp.setMilestones(milestones.stream().map(m -> {
                        MilestoneDto d = new MilestoneDto();
                        d.setStepIndex(m.getStepIndex());
                        d.setDayOffset(m.getDayOffset());
                        d.setTargetCigarettes(m.getTargetCigarettes());
                        return d;
                    }).collect(Collectors.toList()));

                    return resp;
                }).collect(Collectors.toList());
    }
}

