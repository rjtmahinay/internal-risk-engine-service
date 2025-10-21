package com.rjtmahinay.underwriting.internal_risk_engine_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Internal Risk Engine Service API")
                        .version("1.0.0")
                        .description("A comprehensive risk assessment service for loan underwriting that evaluates " +
                                "loan applications and provides risk scores, approval recommendations, and interest rate suggestions.")
                        .contact(new Contact()
                                .name("Risk Engine Team")
                                .email("risk-engine@company.com")
                                .url("https://company.com/risk-engine"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server"),
                        new Server()
                                .url("https://api.company.com")
                                .description("Production Server")
                ));
    }
}
