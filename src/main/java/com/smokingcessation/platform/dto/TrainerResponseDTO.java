package com.smokingcessation.platform.dto;

import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.enums.TrainerRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponseDTO {
    private String certification;
    private TrainerRequestStatus status;
    private double height;
    private double weight;
    private String bio;
    private double yoe;
    private String avatarUrl;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long id;
}
