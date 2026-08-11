# 🤝 Microservice Implementation Guide for Team Partners

This guide explains how each team member can build, secure, and integrate their assigned Spring Boot microservice into the **Central API Gateway & Eureka Ecosystem**.

---

## 🏗️ Architecture Overview for Partners

```
[ Frontend Client App ] 
         │ (Port 8080 - HTTP + Authorization: Bearer <JWT>)
         ▼
 ┌────────────────────────────────────────────────────────┐
 │           Central API Gateway (Port 8080)             │
 │ - Validates JWT Token                                  │
 │ - Auto-injects X-API-KEY: hospital-internal-secret-key │
 └──────────────────────────┬─────────────────────────────┘
                            │ (Internal Forwarding)
         ┌──────────────────┼──────────────────┐
         ▼                  ▼                  ▼
┌──────────────────┐┌──────────────────┐┌──────────────────┐
│ Patient Service  ││  Doctor Service  ││Appointment Serv. │
│   (Port 8082)    ││   (Port 8083)    ││   (Port 8084)    │
└──────────────────┘└──────────────────┘└──────────────────┘
```

---

## 🔑 Security Requirement: Direct Access Protection (API Key Filter)

Every individual microservice MUST reject direct calls from external clients that do not come through the API Gateway.

### Implementation Step for ALL Microservices:
Add an `ApiKeyAuthenticationFilter` to your Spring Boot project:

```java
package com.hospital.yourpackage.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    @Value("${app.api-key:hospital-internal-secret-key-2026}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Allow Swagger UI and API Docs without API Key check
        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs") || path.contains("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestApiKey = request.getHeader(API_KEY_HEADER);

        if (requestApiKey == null || !requestApiKey.equals(validApiKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Direct access forbidden. Requests must originate from API Gateway.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
```

---

## 📋 Microservice Breakdown & Step-by-Step Instructions

### 👤 Student 2: Patient Service
- **Directory**: `/patient-service`
- **Port**: `8082`
- **Gateway Route**: `/api/v1/patients/**`
- **Database**: PostgreSQL (`hospital_db`)
- **Required Endpoints (Minimum 3-4)**:
  1. `POST /api/v1/patients` — Register/Create new patient profile
  2. `GET /api/v1/patients/{id}` — Get patient by ID
  3. `GET /api/v1/patients` — List all patients (Admin only)
  4. `POST /api/v1/patients/{id}/medical-history` — Add medical record
- **Dependencies**: Spring Web, Spring Security, Spring Data JPA, PostgreSQL Driver, Eureka Client, Springdoc OpenAPI.

---

### 🩺 Student 3: Doctor Service
- **Directory**: `/doctor-service`
- **Port**: `8083`
- **Gateway Route**: `/api/v1/doctors/**`
- **Database**: PostgreSQL (`hospital_db`)
- **Required Endpoints (Minimum 3-4)**:
  1. `POST /api/v1/doctors` — Add doctor profile & specialty
  2. `GET /api/v1/doctors` — Get list of doctors
  3. `GET /api/v1/doctors/{id}` — Get doctor by ID
  4. `PUT /api/v1/doctors/{id}/availability` — Update doctor schedule/availability
- **Dependencies**: Spring Web, Spring Security, Spring Data JPA, PostgreSQL Driver, Eureka Client, Springdoc OpenAPI.

---

### 📅 Student 4: Appointment Service
- **Directory**: `/appointment-service`
- **Port**: `8084`
- **Gateway Route**: `/api/v1/appointments/**`
- **Database**: PostgreSQL (`hospital_db`)
- **Required Endpoints (Minimum 3-4)**:
  1. `POST /api/v1/appointments` — Book an appointment (`patientId`, `doctorId`, `appointmentTime`)
  2. `GET /api/v1/appointments/{id}` — Get appointment details
  3. `GET /api/v1/appointments/patient/{patientId}` — List appointments by patient
  4. `PUT /api/v1/appointments/{id}/cancel` — Cancel appointment
- **Dependencies**: Spring Web, Spring Security, Spring Data JPA, PostgreSQL Driver, Eureka Client, Springdoc OpenAPI.

---

### 🔔 Student 5: Notification Service
- **Directory**: `/notification-service`
- **Port**: `8085`
- **Gateway Route**: `/api/v1/notifications/**`
- **Database**: MongoDB (`auth_db` or `notification_db`)
- **Required Endpoints (Minimum 3-4)**:
  1. `POST /api/v1/notifications/email` — Send email notification
  2. `POST /api/v1/notifications/sms` — Send SMS alert
  3. `GET /api/v1/notifications/user/{userId}` — Get notification history
  4. `PUT /api/v1/notifications/{id}/read` — Mark notification as read
- **Dependencies**: Spring Web, Spring Security, Spring Data MongoDB, Eureka Client, Springdoc OpenAPI.

---

## 🐳 Dockerfile Template for Partners

Copy this `Dockerfile` into your microservice directory (e.g. `/doctor-service/Dockerfile`):

```dockerfile
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
COPY auth-service/pom.xml auth-service/

COPY doctor-service doctor-service
RUN mvn clean package -pl doctor-service -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/doctor-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 📚 OpenAPI / Swagger UI Setup for Partners

Add this `OpenApiConfig` class to your microservice:

```java
package com.hospital.yourpackage.config;

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
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Your Service Name API").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth").addList("X-API-KEY"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                        .addSecuritySchemes("X-API-KEY", new SecurityScheme().type(SecurityScheme.Type.APIKEY).name("X-API-KEY").in(SecurityScheme.In.HEADER)));
    }
}
```

---

## 🧪 How to Test Your Service
1. Run your microservice locally.
2. Direct request to `http://localhost:808X/api/v1/...` without `X-API-KEY` header should return **`401 Unauthorized`**.
3. Request through Gateway `http://localhost:8080/api/v1/...` with `Authorization: Bearer <JWT>` header will pass through successfully!
