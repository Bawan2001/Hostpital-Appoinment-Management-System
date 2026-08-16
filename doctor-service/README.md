# Doctor Service (`doctor-service`)

## 📌 Overview
The **Doctor Service** is responsible for managing doctor profiles, specializations, consultation schedules, and availability tracking in the Hospital Appointment System.

## 🛠️ Tech Stack
- Java 21 & Spring Boot 3.2.3
- Spring Data MongoDB (`doctor_db` database)
- Spring Security 6 (API Key Authentication)
- Spring Cloud Netflix Eureka Client
- Springdoc OpenAPI (Swagger UI)

## 🔑 Security & Headers
Every request to endpoints in this service must pass the API Key filter:
- **Header**: `X-API-KEY: hospital-internal-secret-key-2026`

## 🚀 Key Endpoints & Features

### 1. Doctor Management
- **Create Doctor Profile**: `POST /api/v1/doctors`
- **Get All Doctors**: `GET /api/v1/doctors`
- **Get Doctor by ID**: `GET /api/v1/doctors/{id}`
- **Update Doctor**: `PUT /api/v1/doctors/{id}`
- **Delete Doctor**: `DELETE /api/v1/doctors/{id}`

### 2. Swagger UI Access
- **URL**: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
