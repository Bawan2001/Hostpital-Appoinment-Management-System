package com.hospital.gateway.config;

import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class SwaggerConfig {

    @Bean
    public Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrls(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = Set.of(
                new AbstractSwaggerUiConfigProperties.SwaggerUrl("Auth Service", "/api/v1/auth/v3/api-docs", "Auth Service"),
                new AbstractSwaggerUiConfigProperties.SwaggerUrl("Patient Service", "/api/v1/patients/v3/api-docs", "Patient Service"),
                new AbstractSwaggerUiConfigProperties.SwaggerUrl("Doctor Service", "/api/v1/doctors/v3/api-docs", "Doctor Service"),
                new AbstractSwaggerUiConfigProperties.SwaggerUrl("Appointment Service", "/api/v1/appointments/v3/api-docs", "Appointment Service"),
                new AbstractSwaggerUiConfigProperties.SwaggerUrl("Notification Service", "/api/v1/notifications/v3/api-docs", "Notification Service")
        );
        swaggerUiConfigProperties.setUrls(urls);
        return urls;
    }
}
