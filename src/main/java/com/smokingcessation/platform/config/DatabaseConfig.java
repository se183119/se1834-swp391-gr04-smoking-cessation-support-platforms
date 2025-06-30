package com.smokingcessation.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Optional;

@Configuration
@EnableJpaRepositories(basePackages = "com.smokingcessation.platform.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfig {

//    /**
//     * Auditor Aware implementation for automatic auditing
//     * Returns current authenticated user or 'system' for system operations
//     */
//    @Bean
//    public AuditorAware<String> springSecurityAuditorAware() {
//        return new SpringSecurityAuditorAware();
//    }

    /**
     * Auditor Aware implementation using Spring Security
     * Automatically sets createdBy and updatedBy fields
     */
    @Bean
    public AuditorAware<String> springSecurityAuditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() ||
                    "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.of("system");
            }

            return Optional.of(authentication.getName());
        };
    }

//    /**
//     * Spring Security Auditor Aware implementation
//     */
//    public static class SpringSecurityAuditorAware implements AuditorAware<String> {
//
//        @Override
//        public Optional<String> getCurrentAuditor() {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (authentication == null || !authentication.isAuthenticated()
//                    || "anonymousUser".equals(authentication.getPrincipal())) {
//                return Optional.of("system");
//            }
//
//            return Optional.of(authentication.getName());
//        }
//    }
}
