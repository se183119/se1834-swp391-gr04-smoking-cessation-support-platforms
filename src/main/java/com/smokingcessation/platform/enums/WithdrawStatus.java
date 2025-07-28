package com.smokingcessation.platform.enums;

public enum WithdrawStatus {
    PENDING("Chờ xử lý"),
    APPROVED("Đã duyệt"),
    REJECTED("Bị từ chối"),
    CANCELLED("Đã hủy");

    private final String displayName;

    WithdrawStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
