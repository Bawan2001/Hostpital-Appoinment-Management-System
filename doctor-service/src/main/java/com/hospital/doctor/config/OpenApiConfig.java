package com.hospital.doctor.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI doctorServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Doctor Service API").description("Microservice for Doctor Catalog, Specialties, and Availability Management").version("v1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth").addList("X-API-KEY"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                        .addSecuritySchemes("X-API-KEY", new SecurityScheme().type(SecurityScheme.Type.APIKEY).name("X-API-KEY").in(SecurityScheme.In.HEADER)));
    }
}
