package com.smokingcessation.platform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PlanResponse {
    private Long planId;
    private LocalDate startDate;
    private BigDecimal quitMonths;
    private List<MilestoneDto> milestones;
}
