package com.smokingcessation.platform.dto;

import lombok.Data;

@Data
public class PackageOrderItemDTO {
    private Long id;
    private String name;
    private String description;
    private int totalDays;
    private String imageUrl;
    private int price;
    private int salePrice;
    private boolean isFixed;
    private Double rating;
    private int totalRating;
    private boolean isActive = true;
}
