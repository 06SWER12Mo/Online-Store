package com.example.demo.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Online Store API")
                        .version("1.0")
                        .description("""
                                Complete Online Store Backend API with JWT Security
                        
                                ## How to Use:
                                1. Login via `/api/auth/v1/login` to get JWT token
                                2. Click Authorize button below
                                3. Enter: `Bearer: {your_token}`
                                4. Now you can access protected endpoints if you're authorized
                                """)
                        .contact(new Contact()
                                .name("Muhammad Ayyad")
                                .email("202303680@bethlehem.edu")
                                .url("https://onlinestore.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org"))
                        .termsOfService("https://store.com/terms"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Local Development Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT token with Bearer prefix.\nExample: Bearer eyJhbGciOiJIUzI1NiIs...")
                                .name("Authorization")
                                .in(SecurityScheme.In.HEADER)));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .packagesToScan("com.example.demo")
                .build();
    }
}