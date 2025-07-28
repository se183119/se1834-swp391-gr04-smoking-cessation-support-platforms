package com.smokingcessation.platform.entity;

import com.smokingcessation.platform.enums.WithdrawStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "withdraws")
public class WithdrawModel extends BaseModel {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private double amount;

    private String bankName;

    private String bankAccount;

    private String imageUrl = "";

    private String bankAccountName;

    private String adminNote;

    @Enumerated(EnumType.STRING)
    private WithdrawStatus status = WithdrawStatus.PENDING;
}
