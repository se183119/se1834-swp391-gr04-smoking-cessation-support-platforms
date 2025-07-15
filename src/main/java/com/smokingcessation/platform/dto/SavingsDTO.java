package com.smokingcessation.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SavingsDTO {
    private int cigarettesReduced;
    private BigDecimal moneySaved;
    private BigDecimal hoursSaved;
    private BigDecimal minutesSaved;
}
