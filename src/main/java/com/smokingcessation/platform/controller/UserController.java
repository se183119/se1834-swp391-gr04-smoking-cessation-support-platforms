package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.dto.request.UpdateProfileRequest;
import com.smokingcessation.platform.dto.response.ApiResponse;
import com.smokingcessation.platform.dto.response.UserResponse;
import com.smokingcessation.platform.entity.User;
import com.smokingcessation.platform.service.AuthService;
import com.smokingcessation.platform.service.JwtService;
import com.smokingcessation.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "User management operations")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, AuthService authService, JwtService jwtService) {
        this.userService = userService;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // ========== Profile Management ==========

    /**
     * Get user profile
     */
    @GetMapping("/profile")
    @Operation(
            summary = "Get user profile",
            description = "Get the profile of the authenticated user"
    )
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(
            HttpServletRequest request) {

        try {
            // Extract and validate token
            String token = extractAndValidateToken(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or missing authentication token"));
            }

            Long userId = jwtService.extractUserId(token);
            Optional<User> userOpt = userService.findById(userId);

            if (userOpt.isPresent()) {
                UserResponse userResponse = UserResponse.from(userOpt.get());
                return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", userResponse));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve profile: " + e.getMessage()));
        }
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    @Operation(
            summary = "Update user profile",
            description = "Update the profile of the authenticated user"
    )
    public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(
            @Valid @RequestBody UpdateProfileRequest updateRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        // Validate request
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + errorMessage));
        }

        try {
            // Extract and validate token
            String token = extractAndValidateToken(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or missing authentication token"));
            }

            Long userId = jwtService.extractUserId(token);

            // Update profile
            AuthService.AuthenticationResult result = authService.updateProfile(
                    userId,
                    updateRequest.getFirstName(),
                    updateRequest.getLastName(),
                    updateRequest.getPhoneNumber(),
                    updateRequest.getBio()
            );

            if (result.isSuccess()) {
                UserResponse userResponse = UserResponse.from(result.getUser());
                return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userResponse));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(result.getMessage()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Profile update failed: " + e.getMessage()));
        }
    }

    // ========== User Lookup ==========

    /**
     * Get user by ID (Admin only - simplified for Phase 2)
     */
    @GetMapping("/{userId}")
    @Operation(
            summary = "Get user by ID",
            description = "Get user information by user ID"
    )
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "User ID")
            @PathVariable Long userId,
            HttpServletRequest request) {

        try {
            // Extract and validate token
            String token = extractAndValidateToken(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or missing authentication token"));
            }

            // For Phase 2, allow users to view their own profile or any profile
            // In Phase 3, add role-based access control
            Optional<User> userOpt = userService.findById(userId);

            if (userOpt.isPresent()) {
                UserResponse userResponse = UserResponse.from(userOpt.get());
                return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userResponse));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User not found"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user: " + e.getMessage()));
        }
    }

    // ========== User Listing ==========

    /**
     * Get all users with pagination
     */
    @GetMapping
    @Operation(
            summary = "Get all users",
            description = "Get paginated list of all active users"
    )
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction")
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {

        try {
            // Extract and validate token
            String token = extractAndValidateToken(request);
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or missing authentication token"));
            }

            // Create pagination and sorting
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            // Get users
            Page<User> usersPage = userService.findAllActiveUsers(pageable);
            Page<UserResponse> userResponsePage = usersPage.map(UserResponse::from);

            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", userResponsePage));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve users: " + e.getMessage()));
        }
    }

    // ========== Utility Methods ==========

    /**
     * Extract and validate JWT token from request
     */
    private String extractAndValidateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String token = jwtService.extractTokenFromHeader(authHeader);

        if (token != null && jwtService.validateToken(token)) {
            return token;
        }

        return null;
    }
}
