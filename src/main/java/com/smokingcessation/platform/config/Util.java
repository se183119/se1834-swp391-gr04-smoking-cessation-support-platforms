package com.smokingcessation.platform.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Util {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule()) // Hỗ trợ LocalDateTime
            .registerModule(new Jdk8Module());    // Hỗ trợ Optional

    public static <T> String toJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi convert object thành JSON", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi convert JSON về object", e);
        }
    }

    public static long generateOrderCode() {
        String timestamp = String.valueOf(System.currentTimeMillis()); // VD: 1718119456789 (13 số)
        return Long.parseLong(timestamp);
    }


}
