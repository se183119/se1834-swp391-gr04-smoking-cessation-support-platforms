package com.smokingcessation.platform.config;

import com.smokingcessation.platform.security.CustomUserDetailsService;
import com.smokingcessation.platform.security.JwtAuthenticationEntryPoint;
import com.smokingcessation.platform.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Authentication provider bean
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Authentication manager bean
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .headers(headers -> headers
//                        .frameOptions(frameOptions -> frameOptions.deny())
//                        .contentTypeOptions(contentTypeOptions -> {})
//                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
//                                .maxAgeInSeconds(31536000)
//                                .includeSubDomains(true)
//                                .preload(true)
//                        )
//                )
//                .authorizeHttpRequests(authz -> authz
//                        // Public endpoints
//                        .requestMatchers("/api/health/**").permitAll()
//                        .requestMatchers("/api/actuator/**").permitAll()
//                        .requestMatchers("/api/swagger-ui/**").permitAll()
//                        .requestMatchers("/api/swagger-ui.html").permitAll()
//                        .requestMatchers("/api/v3/api-docs/**").permitAll()
//                        .requestMatchers("/api/error").permitAll()
//                        // All other endpoints require authentication
////                        .anyRequest().authenticated()
//                                .anyRequest().permitAll()
//                );
//
//        return http.build();
//    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOriginPatterns(Arrays.asList(
//                "http://localhost:3000",
//                "http://localhost:4200",
//                "http://localhost:8080",
//                "https://*.herokuapp.com"
//        ));
//        configuration.setAllowedMethods(Arrays.asList(
//                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
//        ));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/api/**", configuration);
//        return source;
//    }

    /**
     * CORS configuration
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow all origins for development (restrict in production)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"
        ));

        // Allow common headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "Origin",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"
        ));

        // Expose headers for client access
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Main security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for REST API
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure session management (stateless for JWT)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configure authentication entry point
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // Configure authorization rules
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints (no authentication required)
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/check-username",
                                "/api/auth/check-email",
                                "/api/health"
                        ).permitAll()

                        // Swagger/OpenAPI endpoints
                        .requestMatchers(
                                "/api/swagger-ui/**",
                                "/api/swagger-ui.html",
                                "/api/v3/api-docs/**",
                                "/api/v3/api-docs.yaml"
                        ).permitAll()

                        // Actuator endpoints
                        .requestMatchers("/api/actuator/**").permitAll()

                        // Authentication endpoints (require valid JWT)
                        .requestMatchers(
                                "/api/auth/refresh",
                                "/api/auth/change-password",
                                "/api/auth/logout",
                                "/api/auth/me"
                        ).authenticated()

                        // User management endpoints (require valid JWT)
                        .requestMatchers("/api/users/**").authenticated()

                        // All other endpoints require authentication
//                        .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )

                // Set authentication provider
                .authenticationProvider(authenticationProvider())

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
