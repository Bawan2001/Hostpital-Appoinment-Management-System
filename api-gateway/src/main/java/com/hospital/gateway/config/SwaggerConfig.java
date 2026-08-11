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
                new AbstractSwaggerUiConfigProperties.SwaggerUrl("Auth Service", "/api/v1/auth/v3/api-docs", "Auth Service")
        );
        swaggerUiConfigProperties.setUrls(urls);
        return urls;
    }
}
