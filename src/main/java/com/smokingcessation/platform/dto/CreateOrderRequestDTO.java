package com.smokingcessation.platform.dto;

import com.smokingcessation.platform.enums.OrderType;
import lombok.Data;

@Data
public class CreateOrderRequestDTO {
    private long PackageId = 0;
    private String returnUrl;
    private String cancelUrl;
    private int amount;
    private long userId;
}
