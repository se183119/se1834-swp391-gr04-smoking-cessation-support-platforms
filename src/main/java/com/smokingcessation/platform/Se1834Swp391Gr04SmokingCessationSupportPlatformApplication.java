package com.smokingcessation.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class Se1834Swp391Gr04SmokingCessationSupportPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(Se1834Swp391Gr04SmokingCessationSupportPlatformApplication.class, args);
        System.out.println("🚀 Smoking Cessation Support Platform started successfully!");
        System.out.println("📋 Health Check: http://localhost:8080/api/health");
        System.out.println("📚 Swagger UI: http://localhost:8080/api/swagger-ui.html");
        System.out.println("⚡ Ready for development!");
    }

}
