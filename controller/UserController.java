package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.entity.Role;
import com.smokingcessation.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "APIs for managing users, registration, profiles and authentication")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register new user", description = "Register a new user account with role MEMBER by default")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user data or registration failed")
    })
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest request) {
        try {
            // Validate required fields
            if (request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty() ||
                request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            User user = new User();
            user.setUsername(request.getUsername().trim());
            user.setEmail(request.getEmail().trim());
            user.setPassword(request.getPassword());
            user.setFullName(request.getFullName());
            user.setPhone(request.getPhone());
            user.setGender(request.getGender());
            user.setAge(request.getAge());
            user.setStatus(User.UserStatus.ACTIVE); // Set trạng thái mặc định là ACTIVE

            Set<Role.RoleName> roles = Set.of(Role.RoleName.MEMBER); // Mặc định là MEMBER
            User savedUser = userService.registerUser(user, roles);

            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi để debug
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user information by user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "User ID", example = "1")
            @PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get user by username", description = "Retrieve user information by username")
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(
            @Parameter(description = "Username", example = "john_doe")
            @PathVariable String username) {
        return userService.findByUsername(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update user profile", description = "Update user profile information")
    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(
            @Parameter(description = "User ID") @PathVariable Long id,
            @RequestBody ProfileUpdateRequest request) {
        try {
            User updatedUser = userService.updateProfile(id, request.getFullName(),
                request.getPhone(), request.getGender(), request.getAge());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get all coaches", description = "Retrieve list of all available coaches")
    @GetMapping("/coaches")
    public ResponseEntity<List<User>> getCoaches() {
        List<User> coaches = userService.findCoaches();
        return ResponseEntity.ok(coaches);
    }

    @Operation(summary = "Get active members", description = "Retrieve list of active members with valid membership")
    @GetMapping("/active-members")
    public ResponseEntity<List<User>> getActiveMembers() {
        List<User> activeMembers = userService.findActiveMembers();
        return ResponseEntity.ok(activeMembers);
    }

    @Operation(summary = "Check username availability", description = "Check if username already exists")
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(
            @Parameter(description = "Username to check") @PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Check email availability", description = "Check if email already exists")
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "Email to check") @PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "[ADMIN] Update user status", description = "Admin function to update user status")
    @PutMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id,
                                               @RequestBody StatusUpdateRequest request) {
        try {
            User updatedUser = userService.updateUserStatus(id, request.getStatus());
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "[ADMIN] Get all users", description = "Admin function to retrieve all users")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    // DTOs
    public static class UserRegistrationRequest {
        private String username;
        private String email;
        private String password;
        private String fullName;
        private String phone;
        private User.Gender gender;
        private Integer age;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public User.Gender getGender() { return gender; }
        public void setGender(User.Gender gender) { this.gender = gender; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
    }

    public static class ProfileUpdateRequest {
        private String fullName;
        private String phone;
        private User.Gender gender;
        private Integer age;

        // Getters and setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public User.Gender getGender() { return gender; }
        public void setGender(User.Gender gender) { this.gender = gender; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
    }

    public static class StatusUpdateRequest {
        private User.UserStatus status;

        public User.UserStatus getStatus() { return status; }
        public void setStatus(User.UserStatus status) { this.status = status; }
    }
}
