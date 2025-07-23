package com.smokingcessation.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smoking Cessation Support Platform API")
                        .description("Comprehensive REST API for the Smoking Cessation Support Platform - Nền tảng hỗ trợ cai nghiện thuốc lá")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SE1834 SWP391 Group 04")
                                .email("se1834.swp391.gr04@example.com")
                                .url("https://github.com/se1834-swp391-gr04"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.smokingcessation.com")
                                // .url("https://scsp.autopass.blog")
                                .description("Production Server")
                ));
    }
}
