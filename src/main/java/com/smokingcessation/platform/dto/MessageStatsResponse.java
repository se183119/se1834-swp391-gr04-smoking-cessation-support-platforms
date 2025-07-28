package com.smokingcessation.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageStatsResponse {
    private long userId;
    private int totalMessages;
    private long rewardAmount; // VND
    private int supportedUsers;
}