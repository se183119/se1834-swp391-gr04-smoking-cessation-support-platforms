package com.smokingcessation.platform.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smokingcessation.platform.dto.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // Log the unauthorized access attempt
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String remoteAddr = getClientIpAddress(request);

        System.err.println("Unauthorized access attempt: " + method + " " + requestURI +
                " from " + remoteAddr + " - " + authException.getMessage());

        // Set response headers
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create error response
        ApiResponse<String> errorResponse = ApiResponse.error(
                "Unauthorized: Authentication token is required to access this resource"
        );

        // Write response
        response.getOutputStream()
                .println(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            // X-Forwarded-For can contain multiple IP addresses, get the first one
            return xForwardedForHeader.split(",")[0].trim();
        }
    }
}