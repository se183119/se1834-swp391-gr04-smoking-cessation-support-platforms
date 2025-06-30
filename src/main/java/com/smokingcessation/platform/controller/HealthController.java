package com.smokingcessation.platform.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Application health monitoring endpoints")
public class HealthController {

    @Value("${spring.application.name:Smoking Cessation Platform}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String version;

    @GetMapping
    @Operation(summary = "Basic health check", description = "Returns basic application health status")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("application", applicationName);
        healthInfo.put("version", version);
        healthInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        healthInfo.put("message", "Smoking Cessation Platform is running successfully");

        return ResponseEntity.ok(healthInfo);
    }

    @GetMapping("/info")
    @Operation(summary = "Application information", description = "Returns detailed application information")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> appInfo = new HashMap<>();

        // Application details
        Map<String, Object> app = new HashMap<>();
        app.put("name", applicationName);
        app.put("version", version);
        app.put("description", "Smoking Cessation Support Platform - Spring Boot Backend");
        app.put("team", "SE1834 Group 4");

        // Build details
        Map<String, Object> build = new HashMap<>();
        build.put("java.version", System.getProperty("java.version"));
        build.put("spring.boot.version", org.springframework.boot.SpringBootVersion.getVersion());
        build.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Environment details
        Map<String, Object> environment = new HashMap<>();
        environment.put("java.home", System.getProperty("java.home"));
        environment.put("os.name", System.getProperty("os.name"));
        environment.put("os.version", System.getProperty("os.version"));
        environment.put("user.timezone", System.getProperty("user.timezone"));

        appInfo.put("application", app);
        appInfo.put("build", build);
        appInfo.put("environment", environment);
        appInfo.put("status", "UP");

        return ResponseEntity.ok(appInfo);
    }
}