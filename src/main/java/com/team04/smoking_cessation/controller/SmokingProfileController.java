package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.entity.SmokingProfile;
import com.team04.smoking_cessation.service.SmokingProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/smoking-profiles")
@Tag(name = "Smoking Profile", description = "User smoking profile management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SmokingProfileController {

    @Autowired
    private SmokingProfileService smokingProfileService;

    @PostMapping
    @Operation(summary = "Create or update smoking profile")
    public ResponseEntity<SmokingProfile> createOrUpdateProfile(@RequestBody Map<String, Object> profileData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        SmokingProfile profile = smokingProfileService.createOrUpdateProfile(email, profileData);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    @Operation(summary = "Get current user's smoking profile")
    public ResponseEntity<SmokingProfile> getCurrentProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        SmokingProfile profile = smokingProfileService.getCurrentProfile(email);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active smoking profile")
    public ResponseEntity<SmokingProfile> getActiveProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        SmokingProfile profile = smokingProfileService.getActiveProfile(email);
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/{profileId}")
    @Operation(summary = "Delete smoking profile")
    public ResponseEntity<Map<String, String>> deleteProfile(@PathVariable Long profileId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        smokingProfileService.deleteProfile(email, profileId);
        return ResponseEntity.ok(Map.of("message", "Profile deleted successfully"));
    }
} 