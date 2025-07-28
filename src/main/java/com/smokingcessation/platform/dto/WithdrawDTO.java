package com.smokingcessation.platform.dto;


import com.smokingcessation.platform.enums.WithdrawStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WithdrawDTO {
    private Long id;
    private double amount;
    private String bankName;
    private String bankAccount;
    private String bankAccountName;
    private WithdrawStatus status;
    private String adminNote;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}