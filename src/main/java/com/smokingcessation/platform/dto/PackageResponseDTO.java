package com.smokingcessation.platform.dto;


import com.smokingcessation.platform.enums.PackageType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PackageResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String featured;
    private int totalDays;
    private String imageUrl;
    private int price;
    private int salePrice;
    private Double rating;
    private int totalRating;
    private boolean isActive = true;
    private PackageType packageType;

}
