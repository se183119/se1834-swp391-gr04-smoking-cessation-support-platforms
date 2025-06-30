package com.smokingcessation.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .servers(getServers())
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("JWT", createAPIKeyScheme()));
    }

    private Info getApiInfo() {
        return new Info()
                .title("Smoking Cessation Support Platform API")
                .description("REST API for Smoking Cessation Support Platform - Comprehensive smoking cessation support system")
                .version(appVersion)
                .contact(getContactInfo())
                .license(getLicenseInfo());
    }

    private Contact getContactInfo() {
        return new Contact()
                .name("SE1834 Group 4")
                .email("se183119@fpt.edu.vn")
                .url("https://github.com/se183119/se1834-swp391-gr04-smoking-cessation-support-platform");
    }

    private License getLicenseInfo() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    private List<Server> getServers() {
        return List.of(
                new Server()
                        .url("http://localhost:8080/api")
                        .description("Development Server"),
                new Server()
                        .url("https://smoking-cessation-platform.herokuapp.com/api")
                        .description("Production Server")
        );
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT Authentication");
    }
}
