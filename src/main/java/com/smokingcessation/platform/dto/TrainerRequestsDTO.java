package com.smokingcessation.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerRequestsDTO {
    private String certification;
    private String bio;
    private Float yoe;
    private long userId;
}
