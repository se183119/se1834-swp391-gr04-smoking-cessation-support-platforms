package com.smokingcessation.platform.dto;

import lombok.Data;

@Data
public class WithdrawUpdateStatusDTO {
    private Long id; // ID của yêu cầu cần cập nhật
    private String status; // NEW status (APPROVED, REJECTED, etc.)
    private String imageUrl; // URL của ảnh hoá đơn chuyển khoản (nếu có)
    private String note; // Ghi chú từ admin
}