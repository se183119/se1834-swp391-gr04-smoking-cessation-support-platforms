package com.smokingcessation.platform.enums;

public enum UserRole {
    ADMIN("Administrator"),
    MODERATOR("Moderator"),
    THERAPIST("Therapist"),
    COACH("Coach"),
    PREMIUM_MEMBER("Premium Member"),
    MEMBER("Member");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean hasAdminPrivileges() {
        return this == ADMIN || this == MODERATOR;
    }

    public boolean canProvideSupport() {
        return this == THERAPIST || this == COACH || hasAdminPrivileges();
    }

    public boolean isPremium() {
        return this == PREMIUM_MEMBER || hasAdminPrivileges();
    }
}
