package com.team04.smoking_cessation.controller;

import com.team04.smoking_cessation.dto.response.UserResponse;
import com.team04.smoking_cessation.entity.User;
import com.team04.smoking_cessation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "User profile and account management APIs")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile")
    public ResponseEntity<UserResponse> updateProfile(@RequestBody Map<String, Object> updateData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        UserResponse updatedUser = userService.updateUserProfile(email, updateData);
        return ResponseEntity.ok(updatedUser);
    }

//    @PostMapping("/avatar")
//    @Operation(summary = "Upload user avatar")
//    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String email = auth.getName();
//        String avatarUrl = userService.uploadAvatar(email, file);
//        return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
//    }
@PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@Operation(summary = "Upload user avatar")
public ResponseEntity<Map<String, String>> uploadAvatar(
        @Parameter(description = "File ảnh avatar", required = true) // <-- Sử dụng @Parameter ở đây
        @RequestParam("file") MultipartFile file) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    String avatarUrl = userService.uploadAvatar(email, file);
    return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
}

    @GetMapping("/dashboard")
    @Operation(summary = "Get user dashboard data")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Map<String, Object> dashboard = userService.getUserDashboard(email);
        return ResponseEntity.ok(dashboard);
    }

    @DeleteMapping("/account")
    @Operation(summary = "Delete user account")
    public ResponseEntity<Map<String, String>> deleteAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        userService.deleteUserAccount(email);
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
}
