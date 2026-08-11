package com.hospital.patient.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI patientServiceOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final String apiKeyHeaderName = "X-API-KEY";

        return new OpenAPI()
                .info(new Info()
                        .title("Patient Service API")
                        .description("Microservice for Patient Demographic Profiles, Addresses, Blood Groups, and Medical History Records.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Hospital System Architecture Team")
                                .email("architecture@hospital.com")))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName)
                        .addList(apiKeyHeaderName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                        .addSecuritySchemes(apiKeyHeaderName, new SecurityScheme()
                                .name(apiKeyHeaderName)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)));
    }
}
