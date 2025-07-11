package com.smokingcessation.platform.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePlanRequest {
    private Long userId;
    private BigDecimal quitMonths;
    private Double yearsSmoking;
    private Integer cigsPerDay;
}