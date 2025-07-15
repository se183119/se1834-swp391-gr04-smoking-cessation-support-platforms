package com.smokingcessation.platform.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}
