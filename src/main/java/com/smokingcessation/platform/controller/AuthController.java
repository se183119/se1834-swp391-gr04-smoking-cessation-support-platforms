package com.smokingcessation.platform.controller;

import com.smokingcessation.platform.dto.request.ChangePasswordRequest;
import com.smokingcessation.platform.dto.request.LoginRequest;
import com.smokingcessation.platform.dto.request.RegisterRequest;
import com.smokingcessation.platform.dto.response.ApiResponse;
import com.smokingcessation.platform.dto.response.AuthResponse;
import com.smokingcessation.platform.dto.response.UserResponse;
import com.smokingcessation.platform.service.AuthService;
import com.smokingcessation.platform.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and authorization operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    // ========== Authentication Endpoints ==========

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticate user with username/email and password"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "423",
                    description = "Account locked"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult) {

        // Validate request
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + errorMessage));
        }

        try {
            // Authenticate user
            AuthService.AuthenticationResult result = authService.authenticate(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
            );

            if (result.isSuccess()) {
                // Create auth response
                UserResponse userResponse = UserResponse.from(result.getUser());
                AuthResponse authResponse = AuthResponse.success(
                        result.getToken(),
                        jwtService.getExpirationTimeInSeconds(),
                        userResponse
                );

                return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
            } else {
                // Handle authentication failure
                HttpStatus status = result.getMessage().contains("locked") ?
                        HttpStatus.LOCKED : HttpStatus.UNAUTHORIZED;

                return ResponseEntity.status(status)
                        .body(ApiResponse.error(result.getMessage()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Authentication failed: " + e.getMessage()));
        }
    }

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    @Operation(
            summary = "User registration",
            description = "Register a new user account"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Registration successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or user already exists"
            )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            BindingResult bindingResult) {

        // Validate request
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + errorMessage));
        }

        // Check password match
        if (!registerRequest.isPasswordMatch()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Passwords do not match"));
        }

        try {
            // Register user
            AuthService.AuthenticationResult result = authService.register(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getFirstName(),
                    registerRequest.getLastName()
            );

            if (result.isSuccess()) {
                // Create auth response
                UserResponse userResponse = UserResponse.from(result.getUser());
                AuthResponse authResponse = AuthResponse.success(
                        result.getToken(),
                        jwtService.getExpirationTimeInSeconds(),
                        userResponse
                );

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success("Registration successful", authResponse));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(result.getMessage()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Refresh token endpoint
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh JWT token",
            description = "Refresh an existing JWT token"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            HttpServletRequest request) {

        try {
            // Extract token from header
            String authHeader = request.getHeader("Authorization");
            String token = jwtService.extractTokenFromHeader(authHeader);

            if (token == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Authorization header is missing or invalid"));
            }

            // Refresh token
            AuthService.AuthenticationResult result = authService.refreshToken(token);

            if (result.isSuccess()) {
                UserResponse userResponse = UserResponse.from(result.getUser());
                AuthResponse authResponse = AuthResponse.success(
                        result.getToken(),
                        jwtService.getExpirationTimeInSeconds(),
                        userResponse
                );

                return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error(result.getMessage()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Token refresh failed: " + e.getMessage()));
        }
    }

    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    @Operation(
            summary = "User logout",
            description = "Logout user (client-side token removal)"
    )
    public ResponseEntity<ApiResponse<String>> logout() {
        // Note: JWT tokens are stateless, so logout is typically handled client-side
        // by removing the token from storage. This endpoint serves as a confirmation.
        return ResponseEntity.ok(ApiResponse.success("Logout successful. Please remove the token from client storage."));
    }

    // ========== Password Management ==========

    /**
     * Change password endpoint
     */
    @PutMapping("/change-password")
    @Operation(
            summary = "Change user password",
            description = "Change the password for the authenticated user"
    )
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            BindingResult bindingResult,
            HttpServletRequest request) {

        // Validate request
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validation error: " + errorMessage));
        }

        // Check password match
        if (!changePasswordRequest.isNewPasswordMatch()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("New passwords do not match"));
        }

        try {
            // Extract user ID from token
            String authHeader = request.getHeader("Authorization");
            String token = jwtService.extractTokenFromHeader(authHeader);

            if (token == null || !jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or missing authentication token"));
            }

            Long userId = jwtService.extractUserId(token);

            // Change password
            AuthService.AuthenticationResult result = authService.changePassword(
                    userId,
                    changePasswordRequest.getCurrentPassword(),
                    changePasswordRequest.getNewPassword()
            );

            if (result.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(result.getMessage()));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Password change failed: " + e.getMessage()));
        }
    }

    // ========== Validation Endpoints ==========

    /**
     * Check username availability
     */
    @GetMapping("/check-username")
    @Operation(
            summary = "Check username availability",
            description = "Check if a username is available for registration"
    )
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameAvailability(
            @Parameter(description = "Username to check")
            @RequestParam String username) {

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Username parameter is required"));
        }

        try {
            boolean isAvailable = authService.isUsernameAvailable(username.trim());
            String message = isAvailable ? "Username is available" : "Username is already taken";

            return ResponseEntity.ok(ApiResponse.success(message, isAvailable));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Username check failed: " + e.getMessage()));
        }
    }

    /**
     * Check email availability
     */
    @GetMapping("/check-email")
    @Operation(
            summary = "Check email availability",
            description = "Check if an email is available for registration"
    )
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(
            @Parameter(description = "Email to check")
            @RequestParam String email) {

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Email parameter is required"));
        }

        try {
            boolean isAvailable = authService.isEmailAvailable(email.trim());
            String message = isAvailable ? "Email is available" : "Email is already registered";

            return ResponseEntity.ok(ApiResponse.success(message, isAvailable));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Email check failed: " + e.getMessage()));
        }
    }

    // ========== Token Information ==========

    /**
     * Get current user information from token
     */
    @GetMapping("/me")
    @Operation(
            summary = "Get current user",
            description = "Get current authenticated user information"
    )
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            HttpServletRequest request) {

        try {
            // Extract token from header
            String authHeader = request.getHeader("Authorization");
            String token = jwtService.extractTokenFromHeader(authHeader);

            if (token == null || !jwtService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Invalid or missing authentication token"));
            }

            // Get user from token
            var userOpt = authService.validateTokenAndGetUser(token);

            if (userOpt.isPresent()) {
                UserResponse userResponse = UserResponse.from(userOpt.get());
                return ResponseEntity.ok(ApiResponse.success("User information retrieved", userResponse));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not found or inactive"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user information: " + e.getMessage()));
        }
    }
}
