package com.smokingcessation.platform.dto;

import lombok.Data;

@Data
public class WithdrawRequestDTO {
    private Long id;
    private double amount;
    private String bankName;
    private String bankAccount;
    private String bankAccountName;
}
