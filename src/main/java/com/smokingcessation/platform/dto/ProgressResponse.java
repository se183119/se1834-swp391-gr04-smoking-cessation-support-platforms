package com.smokingcessation.platform.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProgressResponse {
    private LocalDate logDate;
    private Integer smoked;
    private String note;
    // getters/setters...
}