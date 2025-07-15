package com.smokingcessation.platform.dto;

import com.smokingcessation.platform.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageCreateUpdateDTO {
    private Long id;
    private String name;
    private String description;
    private int totalDays;
    private String featured;
    private String imageUrl;
    private int price;
    private int salePrice;
    private boolean isActive = true;
    private PackageType packageType;
    private long userId;
}
